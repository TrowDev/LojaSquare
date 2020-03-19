# Loja Square
A Loja Square é uma plataforma para gerenciar as vendas e entregas de vips e cash, tudo isso de forma automática!<br>
Muitos donos de servidores, para manter seu servidor online, precisam vender itens, vips ou cash.<br>
Para realizarem essas vendas, utilizam a internet (web).<br>
Vão atrás de programadores web para fazerem um site que atenda a sua necessidade... <br>
E, infeizmente, muitas das vezes, esses sites custam caro para o dono do servidor.<br>
Pensando nisso, buscando atender as necessidades primordias da maioria dos donos de servidores, a Loja Square surgiu!<br>
Comparado ao Netflix, o cliente Loja Square tem uma assinatura e, enquanto essa assinatura for válida, o site dele fica online com a gente!<br>
Ele terá, com a gente, de forma prática, rápida e barata, um site responsivo, prático, bonito e dinâmico!<br>
Falei que isso sai barato para você, dono de servidor?!<br>
Além da preocupação do site (que agora está resolvida), você não precisará se preocupar também com a entrega!<br>
Fazemos isso para você!<br>
Assim que o pagamento de algum produto do seu site for aprovado, nós ficamos sabendo e realizamos a entrega para o seu player,
dentro do seu servidor!

# Plugin Loja Square
O plugin é usado para realizar a ativação automática dos produtos vendidos no seu site.<br>
O plugin realiza uma conexão na api da Loja Square e, com sua KEY API informada na config.yml, ele busca as entregas que devem ser feitas para o seu servidor (é identificado o servidor pela KEY API).<br>

# Dúvidas Frequentes:
# Quais meios de pagamento a Loja Square suporta?
° PayPal<br>
° PagSeguro<br>
° Mercado Pago<br>

# O que mais a plataforma oferece?!
Com várias características, as que tem mais destaque são:<br>
Conta com um sistema de notícias (para você conseguir ter uma melhor comunicação com seus players, tanto in-game quanto fora do jogo). <br>
Em paralelo ao sistema de notícias, oferecemos também:<br>
° Página de apresentação da Equipe do servidor.<br>
° Templates para sua escolha.<br>
° Estilização prática (fácil mudar as cores no site).<br>
° Sem limites de venda ou de produtos na loja!<br>
° Sistema de cupom de desconto para produtos específicos!<br>
° Status atual do servidor (Online/Offline e n° de players).<br>
° Múltiplos servidores (Factions/RankUP/FullPvP)<br>
° Loja dividida por Categorias<br>
Entre vários outros sistemas que são adicionados!<br>

# Contratando meu site com vocês, precisarei pagar uma hospedagem?!
Não para seu site! <br>
Mantemos seu site online aqui, na nossa plataforma.<br>

# API para Desenvolvedores - apresentarei os Endpoints e sua explicação
A API pode ser acessada por meio da URL: https://api.lojasquare.com.br//v1/<endpoint>
Para conseguir acessar, é necessário informar no header o parâmetro "AUTHORIZATION" com a Key-API do servidor.

**GET - Lista de produtos para entregar**
- Endpoint: **queue/(usuário)**
- URI: **https://api.lojasquare.com.br//v1/queue/(player)**
- Obtem a lista de produtos para entregar a um respectivo usuário.

Para obter a lista de ordens de entrega de **TODOS** os players de uma única vez, use no parâmetro **"player"** um * .
Exemplo:
https://api.lojasquare.com.br//v1/queue/*

**Atualizar o status de uma ordem para Entregue:**
- Endpoint: **queue/(player)/(ID de Entrega)**
- Atualiza o status de uma entrega para Entregue.
<hr>

**GET - Lista de notícias**
- Endpoint: **noticias/(página)**
- URI: **https://api.lojasquare.com.br//v1/noticias/(página)**
- Obtem a lista de notícias postadas, em ordem decrescente.
<hr>

**GET - Lista de produtos**
- Endpoint: **produtos**
- Obtem a lista completa de todos os produtos.
- URI: **https://api.lojasquare.com.br//v1/produtos**
- Endpoint: **produtos/(página)**
- URI: **https://api.lojasquare.com.br//v1/produtos/(página)**
- Obtem a lista de produtos criados, em ordem de posição definida na hora de criar o produto.
<hr>

**GET - Info de produto X**
- Endpoint: **produto/(ID-do-Produto)**
- Obtem as informações de um produto específico
- URI: **https://api.lojasquare.com.br//v1/produto/(ID-do-Produto)**
<hr>

**GET - Lista da Equipe**
- Endpoint: **equipe**
- Obtem a lista de membros da equipe
- URI: **https://api.lojasquare.com.br//v1/equipe**
- Endpoint: **equipe/(cargo)**
- Obtem a lista de membros da equipe de um cargo específico **X**
- URI: **https://api.lojasquare.com.br//v1/equipe/(CARGO)**
<hr>

**GET - Lista de Categorias**
- Endpoint: **categorias**
- Obtem a lista de categorias da loja
- URI: **https://api.lojasquare.com.br//v1/categorias**
- Endpoint: **categorias/(ID-SubServidor)**
- Obtem a lista de categorias de um sub-servidor específico (Criado no Painel > Produtos > Servidores)
- URI: **https://api.lojasquare.com.br//v1/categorias/(ID-SubServidor)**
<hr>

**GET - Lista de SubServidores**
- Endpoint: **subservidores**
- Obtem a lista de categorias da loja
- URI: **https://api.lojasquare.com.br//v1/subservidores**
<hr>

**PUT - Validar Cupom X**
- Endpoint: **cupom/(CUPOM)/(GRUPO-DO-PRODUTO)**
- CUPOM = Cupom a ser validado.
- GRUPO-DO-PRODUTO = Grupo identificador do produto (informado ao criar o produto).
- Obtem as informações do cupom se o cupom for validado.
- URI: **https://api.lojasquare.com.br//v1/cupom/(CUPOM)/(GRUPO-DO-PRODUTO)**
<hr>

**POST - Checkout de compra - DIFERENCIADO**
- URI: **https://www.lojasquare.com.br/gateways/checkout.php**
- Parâmetros necessários:
° Inputs do tipo "hidden":
  - name="servidor"               | value: "Nome do Servidor"
  - name="id"                     | value: "ID do produto"
  - name="qnt"                    | value: "Quantidade adquirida"
  - name="player"                 | value: "Nick do player"
  - name="gateway"                | value: "Gateway escolhido"
    **OBS: Gateways válidos: "MercadoPago", "PayPal", "PagSeguro"**
  - name="cupomON"                | value: "Cupom utilizado"
    **OBS: Se o player não usar nenhum cupom, não precisa criar o input "cupomON".**
<hr>

