
# Pedidos - Guarani

Projeto para a vaga de Software Developer na Guarani Sistemas.
## Pré-requisitos

- Java 17 ou superior

- PostgreSQL 14 ou superior

- IDE de preferência

## Como rodar localmente

1. Clone o repositório

```bash
  git clone https://github.com/JeanCremonezzi/guarani-api-pedido.git
```

2. Instalar as dependências do projeto utilizando o Maven


3. Crie o banco de dados
```bash
  CREATE DATABASE pedidos;
```

4. Atualize o arquivo application.properties com as credenciais corretas do seu banco
```bash
 spring.datasource.url=jdbc:postgresql://localhost:5432/pedidos
 spring.datasource.username=seu_usuario
 spring.datasource.password=sua_senha
```

5. Acesse o projeto através de uma IDE (Opcional)


6. Execute o Projeto

OBS: **_A chave privada (app.key) foi enviada para este repositório apenas para 
facilitar a configuração no ambiente de teste._**

## Documentação da API

[Documentação no Swagger](http://localhost:8080/swagger-ui/index.html)

## Usando a API
Para utilizar os endpoints protegidos da API, é necessário realizar a autenticação e utilizar o token JWT retornado no login.

O projeto já possui um usuário administrador pré-configurado para fins de teste:

```bash
 username: admin

 password: admin
```

Após autenticar-se, será retornado um token JWT. Esse token deve ser incluído no cabeçalho das requisições aos endpoints protegidos.

## Funcionalidades

* Gerenciamento de Pedidos
    - [x]  CRUD completo de pedidos
    - [x]  Filtro por status, data de criação e valor
    - [x]  Consulta detalhada com produtos, quantidades, valores e status

* Gerenciamento de Produtos
    - [x]  CRUD completo de produtos
    - [x]  Consulta com preço, descrição e categoria
    - [x]  Filtro por nome, categoria e preço

* Autenticação e Autorização
    - [x]  Spring Security implementado
    - [x]  Perfis de usuário: Admin, Cliente, Operador
    - [x]  Controle de acesso por permissões (ex: apenas admin pode excluir pedido)

* Cálculo de Preços
    - [x]  Cálculo automático do total com descontos e taxas
    - [x]  Recalcular ao alterar quantidade

* Documentação
    - [x]  Swagger/OpenAPI disponível

* Relatórios (Opcional)
    - [ ]  Relatório de vendas por período
    - [ ]  Relatório de pedidos por status

* Integração de Pagamentos
    - [ ]  Suporte a cartão de crédito
    - [ ]  Suporte a boleto
    - [ ]  Suporte a transferência bancária

* Testes (Opcional)
    - [ ]  Testes unitários com JUnit e Mockito
    - [ ]  Testes de integração entre módulos
    - [ ]  Testes de carga e performance