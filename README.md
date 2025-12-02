# MineP2P

## Introdução 
O MineP2P é um mod para Minecraft que altera o funcionamento tradicional do modo LAN. Em vez de encerrar o mundo quando o jogador anfitrião sai, o mod possibilita transferir a hospedagem do mundo para outro jogador conectado, permitindo que todos continuem jogando sem interrupções.

A ideia surgiu para resolver uma limitação clássica do modo LAN: se o host sair, a sessão termina para todos. Com o MineP2P, o mundo é enviado dinamicamente para outro jogador, que assume automaticamente o papel de novo anfitrião. Isso torna a experiência de jogo muito mais fluida, especialmente em mundos cooperativos entre amigos que não querem depender de uma única máquina para manter a sessão viva.

Atualmente o mod está em fase de testes, e a transferência precisa ser iniciada manualmente. Antes do anfitrião se desconectar, é necessário executar o comando no próprio Minecraft:
```
/transferhost <ip_destino>
``` 

Após isso, o mundo é transmitido para o jogador especificado, que ao receber os dados passa a hospedar a LAN.

O objetivo final é construir um sistema totalmente automático, capaz de escolher o melhor host disponível e gerenciar transferências sem intervenção manual.

O MineP2P pretende transformar o modo LAN em algo mais resiliente, cooperativo e realmente multijogador contínuo.

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