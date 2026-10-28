# Portal de Estágios

Aplicação Java de console para aproximar estudantes de oportunidades de estágio.
Esta versão corresponde à **Entrega 1**: domínio, regras de negócio, persistência
PostgreSQL por JDBC e seleção simulada de identidade.

## Integrantes

Preencher antes de enviar:

- Guilherme Gomes Pinho — 10755529
- Moabe Guedes da Silva Rego — 10748053
- Leonardo Fiori — 10736628

## Requisitos

- Java 17 ou superior
- Maven 3.8 ou superior
- PostgreSQL acessível por JDBC (o projeto está configurado para o Supabase)

## Como executar

Configure o acesso ao banco Supabase e execute os comandos na raiz do projeto. A
aplicação sempre usa JDBC, portanto o banco precisa estar configurado e acessível para
iniciar.

A conexão padrão usa o Session pooler configurado em
[`ConexaoSupabase.java`](src/main/java/dao/jdbc/ConexaoSupabase.java). Defina a
senha no ambiente; não a grave no código, em arquivos do repositório, no README ou no
ZIP enviado:

```bash
export SUPABASE_DB_PASSWORD='sua-senha-do-banco'
export SUPABASE_JDBC_URL='jdbc:postgresql://HOST:PORTA/postgres?sslmode=require'
export SUPABASE_DB_USER='USUARIO_DO_POOLER'
mvn clean compile
mvn exec:java -Dexec.mainClass=app.Main
```

Execute os comandos no mesmo terminal. O usuário e a URL padrão podem ser substituídos
pelas variáveis `SUPABASE_DB_USER` e `SUPABASE_JDBC_URL`, usando os valores exibidos
no painel Supabase em **Connect → Session pooler**. Se a URL e o usuário padrão já
estiverem corretos, basta configurar a senha. Na primeira execução, é necessário que o
usuário do banco tenha permissão para criar as tabelas do esquema `public`.

## Dados de demonstração

O script [estrutura-e-dados.sql](src/main/resources/db/estrutura-e-dados.sql) cria as
tabelas, restrições e registros fictícios. A aplicação executa o script na inicialização
da aplicação; também é possível executá-lo manualmente no SQL Editor do Supabase.
Os comandos são preparados para poderem ser reexecutados sem duplicar os dados de
demonstração existentes.

Contas fictícias disponíveis para selecionar no menu:

| Perfil | E-mail |
|---|---|
| Administrador | `admin@portal.com` |
| Empresa | `rh@acme.com` |
| Empresa | `rh@beta.com` |
| Estudante | `joao@aluno.edu` |
| Estudante | `ana@aluno.edu` |

A seleção de uma conta é apenas uma **identidade simulada** para demonstrar perfis e
regras. Não há senha nem autenticação nesta entrega.

## Organização e arquitetura

O caminho de uma operação é:

**Menu (apresentação) → Service (regras de negócio) → interface DAO → implementação JDBC → PostgreSQL**

- `app`: menus, entrada e apresentação dos resultados; não contém SQL. `Main` monta as camadas.
- `model`: entidades, associações e enums do domínio (classes só com dados).
- `service`: uma classe por assunto (`UsuarioService`, `AreaService`, `VagaService`,
  `CandidaturaService`, `RelatorioService`). Validam entradas, perfil e propriedade dos
  dados e aplicam as regras de negócio. `Validacao` reúne as verificações repetidas.
- `dao`: interfaces de persistência, independentes do banco (não mencionam SQL).
- `dao.jdbc`: implementações JDBC das interfaces, mapeamento de resultados
  (`JdbcMapeadores`), conexão (`ConexaoSupabase`) e inicialização do banco.
- `exception`: erros de acesso, regras de negócio e persistência.

As dependências são recebidas por construtor e montadas em
[`Main`](src/main/java/app/Main.java): conexão → DAOs → serviços → menus. Cada serviço
recebe apenas as interfaces DAO de que precisa, o que permite trocar a implementação
(por exemplo, por uma em memória) e testar as regras isoladamente.

As vagas aceitam carga horária semanal de 1 a 30 horas.

O diagrama de classes está em [diagrama-classes.puml](diagrama-classes.puml).

### Exemplo: candidatura

1. O menu estudante lê o ID da vaga.
2. `CandidaturaService` valida se o usuário selecionado é estudante e se a vaga existe
   e está aberta; verifica também se já existe candidatura para o par estudante–vaga.
3. `CandidaturaDAO` define o contrato de gravação; `JdbcCandidaturaDAO` executa o `INSERT`
   parametrizado.
4. O PostgreSQL aplica chaves estrangeiras e a restrição `UNIQUE (estudante_id, vaga_id)`.
5. O menu apresenta a confirmação ou uma mensagem amigável para uma falha esperada.

### Persistência e integridade

O esquema define e-mails únicos, nome de área único sem diferenciar maiúsculas/minúsculas,
chaves estrangeiras entre usuários e perfis, vagas e empresas/áreas, e candidaturas,
estudantes e vagas. A tabela de candidaturas também restringe a repetição do par
estudante–vaga.

Os DAOs usam `PreparedStatement`, fecham conexões, comandos e resultados com
`try-with-resources` e usam transações quando uma operação grava mais de uma tabela.
Os DAOs JDBC capturam `SQLException` e a convertem em `PersistenciaException`, que o menu
trata sem encerrar a aplicação. Duplicidades (e-mail, nome de área e candidatura) e uso de
área por vagas são verificados pelo serviço antes de gravar, com mensagem clara; as
restrições do banco continuam como garantia final. Entradas numéricas e opções inválidas
são solicitadas novamente.

### Interface funcional e lambda

Em `VagaService.listarAbertas`, `Predicate<Vaga>` é a interface funcional (um único método,
`test`, que devolve `true` ou `false`). A lambda descreve o critério "a vaga atende aos
filtros de área e modalidade escolhidos?" e `stream().filter(...)` a aplica a cada vaga
aberta. Uma só lambda cobre todas as combinações (sem filtro, só área, só modalidade ou
ambos), sem uma ramificação de código para cada caso.

## Regras demonstráveis

- Somente estudante pode se candidatar; a vaga precisa estar aberta e não pode haver
  candidatura duplicada para o mesmo estudante e vaga.
- Somente a empresa proprietária pode editar ou encerrar uma vaga.
- Somente administrador pode cadastrar, editar ou excluir áreas; uma área referenciada
  por vaga não pode ser excluída.
- Candidaturas já realizadas permanecem após o encerramento da vaga.
- A consulta pública lista vagas abertas e permite filtrar por área e modalidade.
- Somente administrador consulta as contagens de estudantes, empresas, vagas abertas,
  vagas encerradas e candidaturas.
- Estudante consulta suas candidaturas; empresa consulta candidatos apenas de suas vagas.

## Roteiro de verificação manual

Execute cada cenário no modo persistente com dados de demonstração. Os IDs são gerados
automaticamente e podem não ser sequenciais; use os IDs apresentados na execução.

1. **Iniciar e consultar:** iniciar a aplicação, selecionar “Consultar vagas abertas” e
   verificar as vagas fictícias. Repetir filtrando por uma área e por modalidade.
2. **Cadastrar estudante e empresa:** cadastrar novos registros com e-mails diferentes.
   Tentar cadastrar novamente um e-mail já existente e verificar a mensagem de duplicidade,
   sem encerramento inesperado.
3. **Estudante:** entrar como estudante, candidatar-se a uma vaga aberta e consultar
   “Minhas candidaturas”. Tentar candidatar-se novamente à mesma vaga e confirmar a recusa.
4. **Empresa:** entrar como empresa, publicar uma vaga, editá-la e conferir em “Minhas vagas”.
   Verificar que a empresa não consegue gerenciar vaga pertencente à outra empresa.
5. **Candidatos:** após candidatura, entrar como empresa proprietária e listar os candidatos
   da vaga. Tentar listar candidatos de vaga de outra empresa e verificar a recusa.
6. **Encerramento:** encerrar uma vaga e confirmar que ela não aparece na consulta aberta,
   não recebe nova candidatura e mantém as candidaturas anteriores.
7. **Administrador/áreas:** cadastrar e editar uma área. Tentar excluir área usada por vaga
   (deve ser impedido); excluir uma área sem vaga vinculada.
8. **Contagens:** consultar as contagens como administrador e confirmar estudantes,
   empresas, vagas abertas, vagas encerradas e candidaturas.
9. **Entradas inválidas:** digitar texto em uma opção numérica, valor fora da faixa, e-mail
   inválido e carga horária não positiva; confirmar que a aplicação informa/solicita correção.
10. **Persistência:** encerrar e iniciar novamente a aplicação e confirmar que cadastros,
    vagas e candidaturas continuam no Supabase.

## Registro de uso de IA

Interação 1: foi solicitada uma revisão do projeto contra o enunciado da Entrega 1. A
revisão identificou a necessidade de separar regras de negócio dos DAOs JDBC e de criar um
script SQL para esquema e dados fictícios. Em seguida, foi solicitada a implementação dessa
separação e do script; os serviços passaram a delegar às interfaces DAO, e foram criadas
implementações JDBC e o arquivo `estrutura-e-dados.sql`.

Interação 2: o grupo pediu uma simplificação do código, por considerá-lo mais sofisticado
do que o conteúdo estudado. A IA apontou os trechos mais complexos e o código foi
reescrito mantendo o comportamento: classes anônimas e genéricos nos DAOs JDBC viraram
JDBC direto com `try-with-resources`; a classe única de serviços foi dividida em cinco
serviços; `Optional`, `switch` com seta, blocos de texto e `instanceof` com padrão foram
substituídos por `null`, `if` e `switch` tradicionais, texto concatenado e cast; a
validação duplicada entre modelo e serviço ficou só no serviço; os DAOs de estudante e
empresa foram unificados em `UsuarioDAO`. O comportamento foi comparado com a versão
anterior executando o mesmo roteiro de entradas num PostgreSQL local.

O grupo deve revisar este registro, acrescentar a ferramenta/data exigida pelo professor
se necessário e garantir que cada integrante consiga explicar e verificar o código.

## Material para envio no Moodle

- Link deste repositório.
- ZIP da versão `entrega-1`, sem credenciais, `.git`, `target/` ou outros arquivos gerados.
- Este README preenchido com os integrantes.
- [Diagrama de classes](diagrama-classes.puml) e [script do banco](src/main/resources/db/estrutura-e-dados.sql).
- Registro da interação de IA e roteiro de verificação acima.
