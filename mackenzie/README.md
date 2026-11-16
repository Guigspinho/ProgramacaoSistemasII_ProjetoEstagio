# Portal de Estágios

Aplicação Java para aproximar estudantes de oportunidades de estágio, construída sobre
**Spring Boot**. A aplicação oferece API REST e interface web com persistência PostgreSQL
por JPA, autenticação por sessão e autorização por perfil e propriedade dos registros.

## Integrantes

- Guilherme Gomes Pinho — 10755529
- Moabe Guedes da Silva Rego — 10748053
- Leonardo Fiori — 10736628

## Requisitos

- Java 17 ou superior
- Maven 3.8 ou superior
- PostgreSQL acessível pelo Spring Data JPA (o projeto está configurado para o Supabase)

## Como executar

Configure o acesso ao banco Supabase e execute os comandos na raiz do projeto. O banco
precisa estar configurado e acessível para a aplicação iniciar.

A conexão padrão usa o Session pooler configurado em
[`application.properties`](src/main/resources/application.properties).
Defina a senha no ambiente; não a grave no código, em arquivos do repositório, no README
ou no ZIP enviado:

```bash
export SUPABASE_DB_PASSWORD='sua-senha-do-banco'
export SUPABASE_JDBC_URL='jdbc:postgresql://HOST:PORTA/postgres?sslmode=require'
export SUPABASE_DB_USER='USUARIO_DO_POOLER'
export APP_BOOTSTRAP_PASSWORD='senha-temporaria-para-contas-legadas'
mvn spring-boot:run
```

No Windows, use `mvn spring-boot:run` (e `set` no lugar de `export`).

Execute os comandos no mesmo terminal. O usuário e a URL padrão podem ser substituídos
pelas variáveis `SUPABASE_DB_USER` e `SUPABASE_JDBC_URL`, usando os valores exibidos
no painel Supabase em **Connect → Session pooler**. Se a URL e o usuário padrão já
estiverem corretos, basta configurar a senha. Na primeira execução, é necessário que o
usuário do banco tenha permissão para criar as tabelas do esquema `public`.

`APP_BOOTSTRAP_PASSWORD` define uma senha temporária para contas antigas que ainda não
possuem hash (incluindo contas fictícias). Use-a somente em ambiente de demonstração e
escolha uma senha diferente da senha do banco. Novos cadastros já gravam o hash BCrypt e
não são alterados por esse bootstrap.

O menu de console abre assim que a aplicação sobe e, ao escolher **Sair**, a aplicação é
encerrada. Para subir apenas a parte web, sem o menu, use
`mvn spring-boot:run -Dspring-boot.run.arguments=--app.console.enabled=false`.

### Interface web

Com o PostgreSQL configurado e a aplicação iniciada sem o console, abra
[`http://localhost:8080/`](http://localhost:8080/) no navegador. A interface consulta a
API na mesma origem. Visitantes podem consultar vagas e criar conta. Após entrar, o portal
mostra apenas as funções do perfil: estudantes consultam vagas e candidaturas; empresas
publicam vagas e gerenciam as próprias; administradores gerenciam áreas e relatórios.

### API REST

Com a aplicação iniciada sem o console, a API fica disponível sob `/api`. O login cria uma
sessão HTTP; os endpoints privados obtêm a identidade dessa sessão, sem aceitar ID de usuário
do cliente. O cadastro público cria somente estudantes ou empresas.

| Método e endpoint | Fluxo | Resposta de sucesso |
|---|---|---|
| `GET /api/auth/csrf` | Obter token CSRF e cookie `XSRF-TOKEN` | `200 OK` |
| `POST /api/auth/login` | Entrar com e-mail e senha | `200 OK` |
| `GET /api/auth/me` | Consultar conta autenticada | `200 OK` |
| `POST /api/auth/logout` | Encerrar a sessão | `204 No Content` |
| `POST /api/usuarios/estudantes` | Cadastrar estudante com senha | `201 Created` |
| `POST /api/usuarios/empresas` | Cadastrar empresa com senha | `201 Created` |
| `GET /api/areas` | Listar áreas | `200 OK` |
| `POST /api/areas` | Administrador cadastra área | `201 Created` |
| `PUT /api/areas/{id}` | Administrador edita área | `200 OK` |
| `DELETE /api/areas/{id}` | Administrador exclui área | `204 No Content` |
| `GET /api/vagas?areaId={id}&modalidade={modalidade}` | Consultar vagas abertas (filtros opcionais) | `200 OK` |
| `POST /api/vagas` | Empresa publica vaga | `201 Created` |
| `GET /api/vagas/minhas` | Empresa lista suas vagas | `200 OK` |
| `PUT /api/vagas/{id}` | Empresa edita vaga própria | `200 OK` |
| `PATCH /api/vagas/{id}/encerramento` | Empresa encerra vaga própria | `204 No Content` |
| `POST /api/vagas/{id}/candidaturas` | Estudante se candidata | `201 Created` |
| `GET /api/candidaturas/minhas` | Estudante lista suas candidaturas | `200 OK` |
| `GET /api/vagas/{id}/candidaturas` | Empresa consulta candidatos de vaga própria | `200 OK` |
| `GET /api/relatorios/contagens` | Administrador consulta contagens | `200 OK` |

O login recebe `{"email":"estudante@exemplo.com","senha":"senha-segura"}`. O corpo
dos cadastros/edições segue os campos do domínio: estudante usa `nome`, `email`, `curso` e
`senha`; empresa usa `nome`, `email`, `nomeEmpresa` e `senha`; área usa `{"nome":"..."}`;
vaga exige `titulo`, `descricao`, `areaId`, `localizacao`, `modalidade` e
`cargaHorariaSemanal`. Erros retornam JSON com status, mensagem e caminho: `400` para
requisição/validação inválida, `403` para perfil ou propriedade não autorizados, `404`
para recurso inexistente e `409` para conflito de regra ou integridade.

O cliente obtém um token em `GET /api/auth/csrf`, mantém o cookie `XSRF-TOKEN` e envia o
valor no cabeçalho `X-XSRF-TOKEN` em cada `POST`, `PUT`, `PATCH` ou `DELETE`, inclusive login,
cadastro e logout. O cookie `JSESSIONID` mantém o usuário autenticado. Senhas são armazenadas
com BCrypt; respostas de usuário nunca incluem o hash.

Os testes (`mvn test`) que carregam o contexto do Spring só executam quando
`SUPABASE_DB_PASSWORD` está definida.

## Dados de demonstração

O script [estrutura-e-dados.sql](src/main/resources/db/estrutura-e-dados.sql) cria as
tabelas, restrições e registros fictícios. A aplicação executa o script na inicialização
da aplicação; também é possível executá-lo manualmente no SQL Editor do Supabase.
Os comandos são preparados para poderem ser reexecutados sem duplicar os dados de
demonstração existentes.

Contas fictícias iniciais:

| Perfil | E-mail |
|---|---|
| Administrador | `admin@portal.com` |
| Empresa | `rh@acme.com` |
| Empresa | `rh@beta.com` |
| Estudante | `joao@aluno.edu` |
| Estudante | `ana@aluno.edu` |

Contas fictícias sem hash recebem a senha temporária configurada em
`APP_BOOTSTRAP_PASSWORD`. O cadastro público não permite criar administradores.

## Organização e arquitetura

O caminho de uma operação é:

**Interface/API → Spring Security (sessão e perfil) → Service (regras de negócio) → interface DAO → implementação JPA → PostgreSQL**

Todos os pacotes abaixo ficam em `com.projeto.estagio.mackenzie` (por exemplo,
`com.projeto.estagio.mackenzie.service`), para serem encontrados pelo Spring Boot.

- `app`: menus, entrada e apresentação dos resultados; não contém SQL. Contém também
   `ConfiguracaoAplicacao` (beans do Spring) e `ConsoleRunner` (abre o menu ao iniciar).
- `security`: configuração de sessão, BCrypt, CSRF e carregamento de contas para autenticação.
- `model`: entidades, associações e enums do domínio.
- `service`: valida perfil, propriedade dos dados e regras de negócio; coordena a operação.
- `dao`: contratos de persistência, independentes do banco.
- `dao.jpa`: implementações JPA dos contratos de persistência.
- `exception`: erros de acesso, regras de negócio e persistência.

As dependências dos menus são recebidas por construtor. Os serviços também oferecem
construtor com interfaces DAO, permitindo trocar as implementações e testar as regras
isoladamente. `ConfiguracaoAplicacao` conecta os serviços às implementações JPA; as regras
de negócio permanecem nos serviços.

As vagas exigem título, descrição, área, localização e modalidade, e aceitam carga horária
semanal de 1 a 30 horas.

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
3. `CandidaturaDAO` define o contrato de gravação; `JpaCandidaturaDAO` persiste a entidade.
4. O PostgreSQL aplica chaves estrangeiras e a restrição `UNIQUE (estudante_id, vaga_id)`.
5. O menu apresenta a confirmação ou uma mensagem amigável para uma falha esperada.

### Persistência e integridade

O esquema define e-mails únicos, nome de área único sem diferenciar maiúsculas/minúsculas,
chaves estrangeiras entre usuários e perfis, vagas e empresas/áreas, e candidaturas,
estudantes e vagas. A tabela de candidaturas também restringe a repetição do par
estudante–vaga.

Os DAOs usam JPA e transações do Spring para persistir as entidades e consultar o PostgreSQL.
Falhas de persistência ou de regra de negócio são tratadas pelo menu. Entradas numéricas e
opções inválidas são solicitadas novamente.

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

## Testes automatizados

Os testes das regras de candidatura ficam em
[CandidaturaServiceTest.java](src/test/java/com/projeto/estagio/mackenzie/service/CandidaturaServiceTest.java).
Eles verificam que:

- uma segunda candidatura do mesmo estudante para a mesma vaga é recusada;
- uma candidatura para uma vaga encerrada é recusada;
- uma empresa não consegue consultar candidatos de vaga pertencente a outra empresa.

As validações de publicação de vaga (campos obrigatórios, área e carga horária) ficam em
[VagaServiceTest.java](src/test/java/com/projeto/estagio/mackenzie/service/VagaServiceTest.java).
O teste de senha BCrypt fica em
[UsuarioServiceTest.java](src/test/java/com/projeto/estagio/mackenzie/service/UsuarioServiceTest.java).
Esse teste também verifica que um e-mail existente é rejeitado ao cadastrar outro perfil,
mesmo com diferenças de maiúsculas/minúsculas ou espaços.
Os testes usam JUnit e Mockito para simular os DAOs; não precisam conectar ao Supabase.
Na raiz do repositório, execute:

```bash
mvn -f mackenzie/pom.xml test
```

Uma execução bem-sucedida termina com código zero. Se uma regra deixar de ser aplicada,
o teste correspondente falha e o Maven mostra a asserção/erro.

## Registro de verificação

Registrem a entrada usada, o resultado esperado e o resultado observado para cada cenário.
As linhas automatizadas abaixo foram executadas localmente; as demais ficam pendentes até
serem realizadas no banco persistente. Não marquem um cenário manual como aprovado sem
registrar o status/corpo HTTP ou a mensagem efetivamente observada.

Para a **Entrega 1**, usem os perfis simulados previstos naquela entrega. Para a
**Entrega 2**, usem contas reais e autenticação por sessão. As contas de demonstração sem
hash precisam de `APP_BOOTSTRAP_PASSWORD`; não registrem a senha, token CSRF ou cookie
de sessão nesta tabela.

Exemplos de entrada para a verificação manual (usem um e-mail ainda não cadastrado e repitam
exatamente o mesmo e-mail no teste de duplicidade):

```json
{"nome":"Ana Teste","email":"ana.teste@example.com","curso":"Sistemas de Informação","senha":"SenhaTeste123!"}
{"nome":"RH Teste","email":"rh.teste@example.com","nomeEmpresa":"Empresa Teste","senha":"SenhaTeste123!"}
{"titulo":"Estágio Java","descricao":"Desenvolvimento back-end","areaId":1,"localizacao":"São Paulo","modalidade":"HIBRIDA","cargaHorariaSemanal":20}
```

Para validar campos ausentes, envie o objeto de vaga sem um campo por vez; para carga
inválida, altere `cargaHorariaSemanal` para `0` e `31`. Substitua `areaId` pelos IDs
existentes no banco. Nas operações de candidatura e propriedade, use IDs reais de vaga,
estudante e empresa; os IDs numéricos das linhas automatizadas abaixo são fixtures dos
testes unitários, não IDs garantidos no banco de demonstração.

| Cenário | Entrada/dados | Resultado esperado | Resultado observado |
|---|---|---|---|
| Candidatura duplicada (automatizado) | Estudante `40`, vaga aberta `30`; candidatar duas vezes | Primeira aceita; segunda recusada; não gravar duplicata | PASS — `rejeitaSegundaCandidaturaDoMesmoEstudanteNaMesmaVaga` |
| Candidatura em vaga encerrada (automatizado) | Estudante `40`, vaga `30` com situação `ENCERRADA` | Recusar e não gravar candidatura | PASS — `rejeitaCandidaturaEmVagaEncerrada` |
| Acesso à vaga de outra empresa (automatizado) | Empresa `20` consulta candidatos da vaga `30`, pertencente à empresa `10` | Recusar acesso e não consultar candidatos | PASS — `impedeEmpresaDeConsultarCandidatosDeVagaDeOutraEmpresa` |
| Cadastro válido | Cadastrar estudante e empresa usando os exemplos de entrada acima | Ambos criados com sucesso | Pendente — preencher após execução |
| E-mail repetido (automatizado) | E-mail existente `ana@exemplo.com`; tentar cadastrar empresa com ` ANA@EXEMPLO.COM ` | Rejeitar cadastro e não criar empresa | PASS — `cadastroRejeitaEmailDuplicadoMesmoComDiferencaDeMaiusculas` |
| Publicar vaga válida (teste de serviço) | Empresa simulada; dados completos; área existente simulada; carga `20` | Serviço cria a vaga aberta e mantém os campos informados | PASS — `publicaVagaComTodosOsCamposObrigatorios` (DAO simulado; sem PostgreSQL) |
| Campos obrigatórios/carga inválida (teste de serviço) | Omitir título, descrição, área, localização ou modalidade; testar carga `0` e `31` | Cada entrada inválida é recusada; nenhuma vaga é criada | PASS — testes de validação em `VagaServiceTest` (DAO simulado; sem PostgreSQL) |
| Publicação via API e persistência da vaga | Empresa autenticada; dados completos; área existente; carga `20` | `201 Created`; vaga aparece após nova consulta | Pendente — preencher após execução |
| Consultar vagas abertas com filtros | Consultar sem filtro e depois por uma área e modalidade existentes | Retornar apenas vagas abertas que correspondam aos filtros | Pendente — preencher após execução |
| Candidatura válida | Estudante autenticado candidata-se a uma vaga aberta | Criar candidatura e exibi-la em “Minhas candidaturas” | Pendente — preencher após execução |
| Edição/encerramento e consulta de vaga de outra empresa | Empresa B tenta editar, encerrar e listar candidatos da vaga da empresa A | Todas as operações são recusadas; dados/candidaturas não são alterados | Pendente — preencher após execução |
| Consulta de candidaturas de outro estudante | Estudante A tenta acessar as candidaturas de estudante B | A API só apresenta as candidaturas do estudante autenticado | Pendente — preencher após execução |
| Exclusão de área em uso | Administrador tenta excluir área referenciada por vaga | Exclusão recusada; área e vaga permanecem | Pendente — preencher após execução |
| Persistência após reiniciar | Criar usuário, vaga e candidatura; reiniciar a aplicação e consultar novamente | Os três registros continuam no PostgreSQL | Pendente — preencher após execução |
| Login, senha inválida e logout (Entrega 2) | Login com credenciais válidas; repetir com senha errada; sair da sessão | Login válido cria sessão; senha errada retorna `401`; logout encerra sessão | Pendente — preencher após execução |
| Acesso sem autenticação/perfil inadequado (Entrega 2) | Sem sessão acessar recurso privado; usar estudante em ação exclusiva de empresa/admin | Sem sessão retorna `401`; perfil inadequado retorna `403` | Pendente — preencher após execução |
| Cadastro público sem administrador/segredo (Entrega 2) | Cadastrar estudante/empresa pela API pública e inspecionar resposta e perfis | Não cria administrador; resposta não contém senha nem hash | Pendente — preencher após execução |

Use IDs realmente apresentados pelo banco — os IDs de demonstração podem variar. Para os
cenários da Entrega 2, obtenha antes o token CSRF em `GET /api/auth/csrf`, mantenha o cookie
e envie o cabeçalho `X-XSRF-TOKEN` nas requisições que alteram dados.

## Registro de uso de IA

O grupo utilizou IA como apoio em três etapas do projeto:

1. **Estruturação inicial:** no início do desenvolvimento, a IA foi usada como apoio
   para organizar rapidamente a estrutura do projeto, separando diretórios e classes
   por responsabilidade. A organização resultante inclui pacotes para apresentação
   (`app`), domínio (`model`), regras de negócio (`service`), contratos de persistência
   (`dao`), implementação JPA (`dao.jpa`) e exceções (`exception`).

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
   valores. A IA ajudou ainda a
   documentar a passagem do modelo de domínio para o modelo de projeto e a organizar
   o roteiro de verificação manual. Essas verificações foram feitas por inspeção dos
   arquivos e do código. O repositório inclui um teste unitário do hash BCrypt; os cenários
   integrados do roteiro ainda devem ser executados pelo grupo com o PostgreSQL configurado.

3. **Simplificação após a conclusão:** o grupo pediu uma simplificação do código, por considerá-lo mais sofisticado
   do que o conteúdo estudado. A IA apontou os trechos mais complexos e o código foi
   reescrito mantendo o comportamento: classes anônimas e genéricos nos DAOs JDBC viraram
   JDBC direto com `try-with-resources`; a classe única de serviços foi dividida em cinco
   serviços; `Optional`, `switch` com seta, blocos de texto e `instanceof` com padrão foram
   substituídos por `null`, `if` e `switch` tradicionais, texto concatenado e cast; a
   validação duplicada entre modelo e serviço ficou só no serviço; os DAOs de estudante e
   empresa foram unificados em `UsuarioDAO`. Na etapa seguinte, a persistência JDBC foi
   substituída por JPA, mantendo os contratos DAO e as regras nos serviços. O comportamento
   da versão anterior foi comparado executando o mesmo roteiro de entradas num PostgreSQL local.
