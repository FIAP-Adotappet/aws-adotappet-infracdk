# Scripts para criação/alteração de infras na AWS com CDK (Cloud Developer Kit)
Esse repositorio possui algumas infras criadas usando o CDK em ambiente Windows64 e abaixo estão algumas dicas para criar e manter as stacks

## Pré Requisitos 
* Instalar o AWS CLI(via instalador msi para windows 64bits): https://docs.aws.amazon.com/cli/v1/userguide/install-windows.html#msi-on-windows
* Instalar o node versão >=12 (Opte por usar o NVM)
* Instalar CDK via packmanager do node (npm install -g aws-cdk)
* Instalar o maven >=3.6 e "settar" as variaveis do sistema %MAVEN_HOME% e path
* Instalar o java >=11 e "settar" as variaveis do sistema %JAVA_HOME% e path
* Ter uma conta AWS e criar um usuario com acesso programatico no AIM
* Ter uma IDE para trabalhar com JAVA (Intellij de preferencia)

## Demais passos (visão macro)
* Configurar o CDK com o usuario programatico da conta AWS
* Criar as stacks via codigo JAVA
* Realizar deploy
* Validar criação dos recursos no cloudformation da conta AWS e demais serviços

### Links Úteis 
* Principal explicação de configuração: https://cdkworkshop.com/15-prerequisites.html
* Git do projeto CDK: https://github.com/aws/aws-cdk
