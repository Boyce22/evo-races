# EvoRaces - Sistema Dinâmico de Evolução de Raças para Minecraft

EvoRaces é um mod revolucionário para Minecraft que transforma a jogabilidade introduzindo um sistema profundo e dinâmico de raças e evolução. Diferente dos mods de raças tradicionais que oferecem bônus estáticos, EvoRaces cria uma experiência imersiva estilo RPG onde sua raça evolui baseada em como você joga, criando caminhos de progressão únicos e escolhas significativas.

## 🎯 Filosofia Central

**"Seu Estilo de Jogo Define Sua Evolução"** - EvoRaces vai além de bônus estáticos de raças para criar um sistema vivo e dinâmico onde suas ações, ambiente e escolhas moldam o desenvolvimento do seu personagem. Toda decisão importa, e cada partida pode levar a diferentes caminhos evolutivos.

## 🧬 Sistemas Principais

### 1. **Sistema de Atributos Personalizados**
Em vez de modificar atributos vanilla, EvoRaces introduz um framework de atributos completamente personalizado:

- **Vitalidade** - Vida máxima e taxa de regeneração
- **Força** - Dano corpo a corpo e capacidade de carga  
- **Agilidade** - Velocidade de movimento, chance de esquiva e velocidade de ataque
- **Intelecto** - Poder mágico, eficácia de feitiços e habilidades especiais
- **Resiliência** - Defesa contra dano e efeitos de status

*Construído na API extensível do Fabric, permitindo integração perfeita e expansão futura.*

### 2. **Sistema de Evolução Dinâmica (O Coração da Rejogabilidade)**
É aqui que EvoRaces se torna verdadeiramente viciante. Sua raça evolui baseada em múltiplos fatores:

#### **Gatilhos de Evolução:**
- **Progressão por Experiência** - Leveling tradicional com marcos específicos por raça
- **Evolução Baseada em Ações** - Matar mobs específicos, usar certas armas, ou realizar ações únicas
- **Influência Ambiental** - Evolução baseada em biomas (Nether vs Overworld vs End)
- **Ciclos Lunares** - Bônus de lua cheia e transformações (estilo RPG clássico)
- **Alinhamento de Estilo de Jogo** - Seu estilo de combate molda seu caminho evolutivo

#### **Exemplos de Caminhos de Evolução:**
- **Elfo** → **Alto Elfo** (magia aprimorada, afinidade com a natureza)
- **Elfo** → **Elfo Negro** (furtividade, maestria em veneno, magia das sombras)
- **Orc** → **Senhor da Guerra** (força brutal, aura de intimidação)
- **Orc** → **Xamã** (conexão espiritual, totens elementares)
- **Humano** → **Cavaleiro** (maestria defensiva, liderança)
- **Humano** → **Ladino** (versatilidade, adaptabilidade)

### 3. **Desvantagens Significativas & Trade-offs**
A maioria dos mods de raças falha aqui oferecendo apenas bônus. EvoRaces garante que toda vantagem venha com um custo real:

#### **Desvantagens Específicas por Raça:**
- **Orc:**
  - ✅ +50% de Dano Corpo a Corpo
  - ❌ -30% de Velocidade de Movimento  
  - ❌ 2x Consumo de Fome
  - ❌ Baixa afinidade com magia

- **Vampiro:**
  - ✅ Visão Noturna & Roubo de Vida
  - ✅ Regeneração de Vida Rápida
  - ❌ Dano da Luz Solar (escala com exposição)
  - ❌ Requer consumo de sangue
  - ❌ Vulnerabilidade ao fogo

- **Anão:**
  - ✅ 3x Velocidade de Mineração
  - ✅ Bônus de navegação subterrânea
  - ❌ -40% de Velocidade de Movimento na superfície
  - ❌ Habilidade de natação pobre
  - ❌ Sensibilidade à luz solar (debuff gradual)

- **Morto-Vivo:**
  - ✅ Não precisa respirar
  - ✅ Zumbis ignoram você
  - ❌ Poções de cura causam dano
  - ❌ Aldeões fogem de você
  - ❌ Água benta/encantamentos causam dano extra

### 4. **Sistema de Habilidades Ativas**
Cada raça ganha habilidades ativas únicas com cooldowns:

#### **Habilidades por Raça:**
- **Elfo** - *Abraço da Natureza* (cura em área + regeneração)
- **Orc** - *Fúria Berserker* (aumento temporário de dano, redução de defesa)
- **Vampiro** - *Passo das Sombras* (teletransporte curto + invisibilidade)
- **Anão** - *Pele de Pedra* (redução de dano + imunidade a knockback)
- **Humano** - *Táticas Adaptativas* (aumento temporário de status baseado na situação)

#### **Implementação:**
- Teclas personalizadas via API do Fabric
- Gerenciamento de cooldown através de dados NBT
- Efeitos visuais e feedback sonoro
- Desbloqueio progressivo de habilidades através da evolução

### 5. **Interação com o Mundo & Imersão**
EvoRaces faz o mundo parecer vivo mudando como ele reage a você:

#### **Reações de NPCs:**
- Aldeões oferecem trocas diferentes baseadas na raça
- Golems de ferro podem atacar raças hostis
- Certos mobs se tornam aliados ou inimigos
- Sacerdotes aldeões oferecem bênçãos/maldições específicas por raça

#### **Interações de Facção:**
- **Mortos-Vivos** - Zumbis, esqueletos e phantoms são neutros
- **Nascidos no Nether** - Piglins oferecem melhores trocas, hoglins são passivos
- **Aquáticos** - Guardiões são amigáveis, golfinhos fornecem bônus
- **Fadas** - Abelhas e criaturas da floresta ajudam você

### 6. **Sistema de Fraquezas Ambientais**
Imersão através da interação ambiental:

#### **Vulnerabilidades Específicas por Raça:**
- **Vampiro** - Luz solar causa queimadura (intensidade baseada na hora do dia)
- **Raças Aquáticas** - Desidratação em terra (precisa de água periódica)
- **Elementais de Fogo** - Água causa dano e fraqueza
- **Habitantes de Montanhas** - Sofrem em oceanos/pântanos profundos
- **Alinhados às Trevas** - Luz de tochas causa desconforto

### 7. **Evolução Baseada em Estilo de Jogo (O Recurso Mais Inovador)**
Sua raça evolui baseada em COMO você joga, não apenas no que você mata:

#### **Caminhos de Evolução Baseados em Estilo de Jogo:**
- **Foco em Arco** → Evolui para especialização em atirador/franco-atirador
- **Foco em Magia** → Torna-se arquimago/ritualista
- **Foco em Corpo a Corpo** → Transforma-se em bruto/berserker
- **Foco em Exploração** → Desenvolve-se em ranger/nômade
- **Foco em Construção** → Evolui para arquiteto/engenheiro

#### **Evolução Híbrida:**
Misture estilos de jogo para criar raças híbridas únicas:
- **Espadachim Mágico** (Magia + Corpo a Corpo)
- **Arqueiro das Sombras** (Furtividade + Arco)
- **Mago de Batalha** (Defesa + Magia)
- **Batedor** (Velocidade + Exploração)

## 🛠️ Implementação Técnica

### **Arquitetura:**
- Construído na **API do Fabric** para máxima compatibilidade
- **Design modular** - Fácil adição de novas raças/evoluções
- **Configuração baseada em dados** - Definições de raças em JSON
- **Sistema orientado a eventos** - Conecta-se ao barramento de eventos do Minecraft

### **Recursos Técnicos Principais:**
1. **Sistema de Capacidade Personalizado** - Persistência de dados de raça
2. **Sincronização de Rede** - Compatibilidade com multiplayer
3. **GUI de Configuração** - Seleção e personalização de raça no jogo
4. **Sistema de Conquistas** - Desafios e recompensas específicos por raça
5. **Camada de Compatibilidade** - Funciona com outros mods populares

## 🎮 Impacto na Jogabilidade

### **Início do Jogo:**
- Seleção de raça define seus pontos fortes/fracos iniciais
- Habilidades básicas desbloqueiam opções iniciais de estilo de jogo
- Consciência ambiental se torna crucial

### **Meio do Jogo:**
- Escolhas de evolução criam caminhos ramificados
- Missões e desafios específicos por raça aparecem
- Interação com o mundo muda baseada na evolução

### **Fim do Jogo:**
- Maestria das habilidades da raça
- Conteúdo end-game único para cada raça
- Chefes e masmorras específicas por raça
- Caminhos de evolução de prestígio

## 🔮 Roteiro Futuro

### **Fase 1 (Atual):**
- Sistema central de raças (Humano, Elfo, Orc, Anão)
- Mecânicas básicas de evolução
- Fundação do sistema de atributos

### **Fase 2:**
- Raças avançadas (Vampiro, Lobisomem, Demônio)
- Árvores de habilidades para cada raça
- Estruturas e masmorras específicas por raça

### **Fase 3:**
- Mecânicas de cruzamento (raças híbridas)
- Sistema de guerras de raças e facções
- Dimensão personalizada com conteúdo específico por raça
- Companheiros NPC de diferentes raças

### **Fase 4:**
- Integração de sistema de magia
- Sistema econômico (comércio baseado em raça)
- Construção de reinos com bônus raciais
- Linhas de missão épicas para cada raça

## 🤝 Comunidade & Modding

EvoRaces é projetado pensando em modders:

- **API Pública** para adicionar raças personalizadas
- **Sistema de templates** para criação rápida de raças
- **Ganchos de eventos** para gatilhos de evolução personalizados
- **Suporte a resource packs** para modelos/texturas personalizados
- **Documentação** para integração de terceiros

## 📊 Filosofia de Balanceamento

1. **Sem Bônus Grátis** - Toda vantagem tem um custo
2. **Escolhas Significativas** - Caminhos de evolução mudam a jogabilidade significativamente
3. **Poder Progressivo** - O poder cresce com a habilidade do jogador
4. **Contra-jogo** - Toda raça tem fraquezas que oponentes podem explorar
5. **Excelência Situacional** - Raças se destacam em cenários específicos

## 🎯 Objetivos de Design

1. **Profundidade em vez de Largura** - Menos raças, mas mais significativas
2. **Agência do Jogador** - Suas escolhas importam
3. **Jogabilidade Emergente** - Interações não planejadas criam histórias
4. **Valor de Rejogabilidade** - Raças diferentes = jogos diferentes
5. **Imersão** - O mundo reage a quem você é

---

**EvoRaces não é apenas mais um mod de raças - é uma reformulação completa da jogabilidade que torna cada partida única, cada escolha significativa, e cada evolução uma história que vale a pena contar.**

*"Torne-se mais que suas origens. Evolua além de seus limites."*