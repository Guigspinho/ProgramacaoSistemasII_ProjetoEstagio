# ProgramacaoSistemasII_ProjetoEstagio

## Executar com Supabase

O projeto usa JDBC diretamente com o PostgreSQL do Supabase. A conexão padrão usa o
Session pooler deste projeto:

- `SUPABASE_DB_PASSWORD`: senha do banco, definida no painel do projeto Supabase

O padrão configurado é `jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require`,
com usuário `postgres.wcljsklgabcmxqjnyalf`. Se o painel **Connect → Session pooler**
mostrar outros valores, substitua-os pelas variáveis `SUPABASE_JDBC_URL` e
`SUPABASE_DB_USER`. Não inclua a senha no código ou no repositório. Configure
`SUPABASE_DB_PASSWORD` no ambiente e execute `mvn compile` e
`mvn exec:java -Dexec.mainClass=app.Main` (ou execute `app.Main` pela IDE). O esquema é
inicializado automaticamente ao iniciar a aplicação. O SQL usado está em
[`src/main/resources/db/estrutura-e-dados.sql`](src/main/resources/db/estrutura-e-dados.sql);
ele cria as tabelas e insere dados fictícios de demonstração de forma idempotente.
O mesmo arquivo pode ser executado manualmente no SQL Editor do Supabase quando
necessário. Os usuários de demonstração incluem `admin@portal.com`, `rh@acme.com`,
`rh@beta.com`, `joao@aluno.edu` e `ana@aluno.edu`.

O endpoint direto `db.<project-ref>.supabase.co` pode resolver apenas para IPv6. Se a
rede da máquina não tiver conectividade IPv6, copie do painel Supabase (**Connect**)
uma URL JDBC do **Session pooler** e defina-a em `SUPABASE_JDBC_URL`; use o usuário
exatamente como exibido pelo painel (pode incluir o identificador do projeto).

Se nenhuma variável de conexão estiver configurada, o programa mantém o modo em memória
para facilitar a execução local; alterações feitas nesse modo não são persistentes. Uma
configuração parcial ou uma falha de conexão é informada ao usuário e não provoca um
fallback silencioso para dados temporários.

O caminho de uma operação é menu → serviço → interface DAO → implementação JDBC → banco.
As regras de negócio ficam nos serviços (`service`); as interfaces de persistência ficam
em [`src/main/java/dao`](src/main/java/dao), e as implementações JDBC em
[`src/main/java/dao/jdbc`](src/main/java/dao/jdbc). As operações de escrita com mais de
uma alteração usam transações JDBC; consultas usam `PreparedStatement`, e conexões,
comandos e resultados são fechados com `try-with-resources`. O banco impõe unicidade de
e-mail e do par estudante–vaga, além de chaves estrangeiras entre perfis, vagas, áreas e
candidaturas. Falhas SQL são convertidas em mensagens de persistência ou de regra de
negócio, que o menu apresenta sem encerrar inesperadamente a aplicação.

Na consulta de vagas abertas, `Predicate<Vaga>` (uma interface funcional) e expressões
lambda combinam filtros opcionais de área e modalidade. Isso evita manter ramificações
separadas para cada combinação de filtros.