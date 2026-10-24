# ProgramacaoSistemasII_ProjetoEstagio

## Executar com Supabase

O projeto usa JDBC diretamente com o PostgreSQL do Supabase. O host, porta, banco e
usuário recebidos para este projeto já são os padrões da conexão. Configure a senha
no ambiente:

- `SUPABASE_DB_PASSWORD`: senha do banco, definida no painel do projeto Supabase

Opcionalmente, `SUPABASE_JDBC_URL` (padrão:
`jdbc:postgresql://db.wcljsklgabcmxqjnyalf.supabase.co:5432/postgres?sslmode=require`)
e `SUPABASE_DB_USER` (padrão: `postgres`) permitem substituir a URL e usuário. Não
inclua a senha no código ou no repositório. Configure `SUPABASE_DB_PASSWORD` no terminal
ou no ambiente de execução da IDE e execute `mvn compile` e
`mvn exec:java -Dexec.mainClass=app.Main` (ou execute `app.Main` pela IDE). O esquema é
criado automaticamente na primeira conexão. São criados dados iniciais para demonstração
apenas quando ainda não existem: `admin@portal.com`, `rh@acme.com`, `rh@beta.com`,
`joao@aluno.edu` e `ana@aluno.edu`.

O endpoint direto `db.<project-ref>.supabase.co` pode resolver apenas para IPv6. Se a
rede da máquina não tiver conectividade IPv6, copie do painel Supabase (**Connect**)
uma URL JDBC do **Session pooler** e defina-a em `SUPABASE_JDBC_URL`; use o usuário
exatamente como exibido pelo painel (pode incluir o identificador do projeto).

Se nenhuma variável de conexão estiver configurada, o programa mantém o modo em memória
para facilitar a execução local; alterações feitas nesse modo não são persistentes. Uma
configuração parcial ou uma falha de conexão é informada ao usuário e não provoca um
fallback silencioso para dados temporários.

As operações de escrita usam transações JDBC; consultas usam `PreparedStatement`, e
conexões, comandos e resultados são fechados com `try-with-resources`. O banco impõe
unicidade de e-mail e do par estudante–vaga, além de chaves estrangeiras entre perfis,
vagas, áreas e candidaturas. Falhas SQL são convertidas em mensagens de persistência
ou de regra de negócio, que o menu apresenta sem encerrar a aplicação.

Na consulta de vagas abertas, `Predicate<Vaga>` (uma interface funcional) e expressões
lambda combinam filtros opcionais de área e modalidade. Isso evita manter ramificações
separadas para cada combinação de filtros.