## Pré-requisitos

Antes de rodar o projeto, você precisa instalar:

### Java 21
No Ubuntu:

```bash
sudo apt update
sudo apt install openjdk-21-jdk
``` 

Verifique com:
``` bash
java -version
``` 

## Instalando e rodando
1. Clone o repositório:
``` bash
git clone https://github.com/LuizF14/minep2p.git
cd minep2p
```
2. Permita a execução do Gradlew (no Linux):
``` bash
chmod +x gradlew
```
3. Gere fontes e prepare o ambiente:
``` bash
./gradlew genSources
```
4. Rode o cliente Minecraft com o mod:
``` bash
./gradlew runClient
```

## Rodando um segundo cliente
Para realizar testes localmente, é possível abrir um segundo client Minecraft com o mod no mesmo computador. Para isso:
1. Clone a branch correta:
``` bash 
git clone -b dev-local https://github.com/LuizF14/minep2p.git
cd minep2p
``` 
2. Permita a execução do Gradlew (no Linux):
``` bash
chmod +x gradlew
```
3. Gere fontes e prepare o ambiente:
``` bash
./gradlew genSources
```
4. Copie o diretório `run/` como `run2/`. 
5. Rode os dois clientes Minecraft:
``` bash
./gradlew runClient
./gradlew runClient2
```