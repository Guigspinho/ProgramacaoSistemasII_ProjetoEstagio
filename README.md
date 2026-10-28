# Portal de Estágios

Aplicação Java de console para aproximar estudantes de oportunidades de estágio.
Esta versão corresponde à **Entrega 1**: domínio, regras de negócio, persistência
PostgreSQL por JDBC e seleção simulada de identidade.

## Integrantes

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
[`ConexaoSupabase.java`](src/main/java/service/jdbc/ConexaoSupabase.java). Defina a
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

- `app`: menus, entrada e apresentação dos resultados; não contém SQL.
- `model`: entidades, associações e enums do domínio.
- `service`: valida perfil, propriedade dos dados e regras de negócio; coordena a operação.
- `dao`: contratos de persistência, independentes do banco.
- `dao.jdbc`: implementações JDBC dos contratos, mapeamento de resultados e inicialização do banco.
- `exception`: erros de acesso, regras de negócio e persistência.

As dependências dos menus são recebidas por construtor. Os serviços também oferecem
construtor com interfaces DAO, permitindo trocar as implementações e testar as regras
isoladamente. `Main` usa a implementação JDBC conectada ao Supabase.

As vagas aceitam carga horária semanal de 1 a 30 horas.

### Modelo de domínio

O modelo representa `Usuario` e seus perfis (`Estudante`, `Empresa` e
`Administrador`), além de `Area`, `Vaga` e `Candidatura`. Uma empresa publica vagas,
uma área classifica vagas, e estudantes realizam candidaturas para vagas. Os estados
e opções controlados são representados pelos enums `Perfil`, `Modalidade` e
`SituacaoVaga`.

Com a construção do modelo de domínio, avançamos para o modelo de projeto para
detalhar as classes, seus atributos e relacionamentos, aproximando a representação
conceitual da estrutura usada na implementação. O diagrama em PlantUML está em
[modelo_projeto.puml](modelo_projeto.puml).

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
Falhas SQL são convertidas em exceções de persistência ou de regra de negócio tratadas
pelo menu. Entradas numéricas e opções inválidas são solicitadas novamente.

### Esquema das tabelas

O esquema PostgreSQL é criado pelo script
[estrutura-e-dados.sql](src/main/resources/db/estrutura-e-dados.sql). As tabelas e
seus principais campos são:

| Tabela | Campos e relacionamentos |
|---|---|
| `usuarios` | `id` (PK), `nome`, `email` (único e normalizado em minúsculas), `perfil` (`ADMINISTRADOR`, `EMPRESA` ou `ESTUDANTE`). |
| `estudantes` | `usuario_id` (PK e FK para `usuarios.id`), `curso`. |
| `empresas` | `usuario_id` (PK e FK para `usuarios.id`), `nome_empresa`. |
| `areas` | `id` (PK), `nome` (único sem diferenciar maiúsculas/minúsculas). |
| `vagas` | `id` (PK), `empresa_id` (FK para `empresas.usuario_id`), `area_id` (FK para `areas.id`), `titulo`, `descricao`, `localizacao`, `modalidade`, `carga_horaria_semanal`, `situacao`. |
| `candidaturas` | `id` (PK), `estudante_id` (FK para `estudantes.usuario_id`), `vaga_id` (FK para `vagas.id`), `data_inscricao`; combinação estudante–vaga única. |

As chaves primárias `id` são geradas automaticamente. `vagas.modalidade` aceita
`PRESENCIAL`, `REMOTA` ou `HIBRIDA`; `situacao` aceita `ABERTA` ou `ENCERRADA` e
começa como `ABERTA`. A carga horária semanal deve estar entre 1 e 30. O perfil
administrador é armazenado diretamente em `usuarios`; apenas estudantes e empresas
possuem tabelas de perfil próprias.

### Interface funcional e lambda

Na consulta de vagas abertas, `Predicate<Vaga>` combina o predicado `Vaga::estaAberta`
com lambdas para os filtros opcionais de área e modalidade. Isso permite compor os
critérios selecionados sem criar uma ramificação de código para cada combinação possível.

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

O grupo utilizou IA como apoio em três etapas do projeto:

1. **Estruturação inicial:** no início do desenvolvimento, a IA foi usada como apoio
   para organizar rapidamente a estrutura do projeto, separando diretórios e classes
   por responsabilidade. A organização resultante inclui pacotes para apresentação
   (`app`), domínio (`model`), regras de negócio (`service`), contratos de persistência
   (`dao`), implementação JDBC (`dao.jdbc`) e exceções (`exception`).

2. **Revisão e verificação em relação ao enunciado:** foi solicitada a análise do
   projeto e de sua documentação frente aos requisitos da entrega. A revisão verificou
   a separação entre serviços e DAOs, a existência do esquema e dos dados fictícios e
   a correspondência entre o SQL, o modelo de domínio e as camadas descritas no README.
   Na revisão inicial, a IA apontou a necessidade de manter as regras de negócio nos
   serviços e o acesso SQL nos DAOs; depois, foi solicitada ajuda para implementar essa
   separação e criar o script de esquema e dados fictícios.
   Na inspeção do código, também foram conferidas regras como restrição de ações por
   perfil e propriedade da vaga, candidatura somente a vagas abertas, prevenção de
   candidaturas duplicadas e limite de carga horária semanal. No banco, foram
   identificadas as chaves estrangeiras, restrições de unicidade e validações de
   valores; nos DAOs JDBC, o uso de consultas parametrizadas. A IA ajudou ainda a
   documentar a passagem do modelo de domínio para o modelo de projeto e a organizar
   o roteiro de verificação manual. Essas verificações foram feitas por inspeção dos
   arquivos e do código; não há testes automatizados no repositório, e os cenários do
   roteiro devem ser executados pelo grupo para validação funcional.

3. **Simplificação após a conclusão:** o grupo pediu uma simplificação do código, por considerá-lo mais sofisticado
   do que o conteúdo estudado. A IA apontou os trechos mais complexos e o código foi
   reescrito mantendo o comportamento: classes anônimas e genéricos nos DAOs JDBC viraram
   JDBC direto com `try-with-resources`; a classe única de serviços foi dividida em cinco
   serviços; `Optional`, `switch` com seta, blocos de texto e `instanceof` com padrão foram
   substituídos por `null`, `if` e `switch` tradicionais, texto concatenado e cast; a
   validação duplicada entre modelo e serviço ficou só no serviço; os DAOs de estudante e
   empresa foram unificados em `UsuarioDAO`. O comportamento foi comparado com a versão
   anterior executando o mesmo roteiro de entradas num PostgreSQL local.
