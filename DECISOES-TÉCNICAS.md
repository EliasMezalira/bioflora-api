# Decisões técnicas de backend do projeto bioflora-api

## Frameworks:
- Quarkus: Framework para criar os serviços backend (similar ao Spring);
- Liquibase: Framework para versionar o banco de dados.
- quarkus-jdbc-postgresql: Conexão com banco de dados (não deve ser usado o autocreate do banco de dados da biblioteca em produção)
- JUnit e Mockito: ferramentas para testes unitários;

## Escolhas de projeto:
- Apesar de as principais ferramentas do mercado possuírem um SDK em Java para utilizarem suas APIs, este projeto escolheu usar o protocolo da OpenAI. Desta forma, mudando apenas os parâmetros de URL e token, podemos consultar em qualquer serviço de IA, facilitando os testes da POC.
- O Quarkus foi criado para dar suporte à compilação nativa, tendo suas bibliotecas já configuradas para esse modo de compilação.
  Através da compilação nativa e do uso da GraalVM, é possível rodar aplicações Java como se fossem programas em C, sem a necessidade de uma JVM.
- Autenticação JWT com chaves privada e pública;
- Manipulação das imagens enviadas para reduzir o tamanho e economizar tokens;
- Hospedado no Render por ter planos gratuitos.
- Pipeline executa os testes no build, gera uma imagem docker e faz o upload