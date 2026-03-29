# EvoRaces

Sistema de raças, evolução e habilidades para Minecraft (Fabric), focado em **gameplay profunda**, **performance** e **extensibilidade simples baseada em dados**.

---

## 🎯 Objetivo

Adicionar ao jogo um sistema de:

* Raças com vantagens e desvantagens reais
* Evolução baseada em ações do jogador
* Habilidades ativas com cooldown
* Atributos customizados
* Progressão com builds diferentes

Tudo isso mantendo **leveza**, **compatibilidade** e **facilidade de manutenção**.

---

## 🧱 Arquitetura (Simplificada e Viável)

O mod segue uma abordagem **modular e orientada a dados**, evitando complexidade desnecessária.

```mermaid
graph TB
    subgraph "Estrutura de Diretórios"
        EVO[evoraces/]
        ATTR[attribute/]
        RACE[race/]
        EVOL[evolution/]
        ABIL[ability/]
        PLAY[player/]
        DATA[data/]
        NET[network/]
        UTIL[util/]
        
        EVO --> ATTR
        EVO --> RACE
        EVO --> EVOL
        EVO --> ABIL
        EVO --> PLAY
        EVO --> DATA
        EVO --> NET
        EVO --> UTIL
    end
    
    subgraph "Configurações JSON"
        RACE_JSON[races.json]
        EVOL_JSON[evolutions.json]
        ABIL_JSON[abilities.json]
        ATTR_JSON[attributes.json]
        
        DATA --> RACE_JSON
        DATA --> EVOL_JSON
        DATA --> ABIL_JSON
        DATA --> ATTR_JSON
    end
```

```
evoraces/
├── attribute/      # Sistema de atributos
├── race/           # Definição de raças
├── evolution/      # Sistema de evolução
├── ability/        # Habilidades ativas
├── player/         # Dados do jogador
├── data/           # Configurações JSON (raças, habilidades, etc)
├── network/        # Sincronização cliente/servidor
└── util/           # Utilidades gerais
```

---

## ⚙️ Tecnologias

* Fabric API (1.20+)
* Java 17+
* JSON (configuração de dados)
* NBT (persistência do jogador)

---

## 🧬 Sistemas Principais

### 🧑‍🤝‍🧑 Raças

Cada raça define:

* Atributos base
* Habilidades iniciais
* Fraquezas
* Efeitos ambientais
* Caminhos de evolução

```mermaid
graph LR
    subgraph "Sistema de Raças"
        RACE_DEF[Definição de Raça]
        ATTR_BASE[Atributos Base]
        ABIL_INIT[Habilidades Iniciais]
        WEAK[Fraquezas]
        ENV[Efeitos Ambientais]
        EVOL_PATH[Caminhos de Evolução]
        
        RACE_DEF --> ATTR_BASE
        RACE_DEF --> ABIL_INIT
        RACE_DEF --> WEAK
        RACE_DEF --> ENV
        RACE_DEF --> EVOL_PATH
    end
    
    subgraph "Exemplo: Elfo"
        ELF[Raça: Elfo]
        ELF_ATTR[Vitalidade: 80<br/>Força: 60<br/>Agilidade: 120<br/>Intelecto: 110]
        ELF_ABIL[Dash, Nature Regen]
        ELF_WEAK[Baixa Defesa]
        ELF_ENV[Bônus em Florestas]
        ELF_EVOL[Para Alto Elfo]
        
        ELF --> ELF_ATTR
        ELF --> ELF_ABIL
        ELF --> ELF_WEAK
        ELF --> ELF_ENV
        ELF --> ELF_EVOL
    end
```

**Exemplo:**

```json
{
  "id": "elf",
  "attributes": {
    "vitality": 80,
    "strength": 60,
    "agility": 120,
    "intellect": 110
  },
  "weaknesses": ["low_defense"],
  "abilities": ["dash", "nature_regen"]
}
```

---

### 📈 Atributos

Sistema customizado com cálculo dinâmico:

* Vitalidade (vida)
* Força (dano)
* Agilidade (velocidade)
* Intelecto (magia/efeitos)
* Resistência (defesa)

```mermaid
graph TD
    subgraph "Sistema de Atributos"
        ATTR_SYS[Sistema de Atributos]
        
        subgraph "Fontes de Modificadores"
            RACE_MOD[Modificador de Raça]
            EVOL_MOD[Modificador de Evolução]
            BUFF_MOD[Modificador de Buffs]
            EQUIP_MOD[Modificador de Equipamento]
        end
        
        subgraph "Atributos Base"
            VIT[Vitalidade]
            STR[Força]
            AGI[Agilidade]
            INT[Intelecto]
            RES[Resistência]
        end
        
        subgraph "Cálculo Final"
            CALC[Calculadora]
            FINAL[Atributos Finais]
            CACHE[Cache de Performance]
        end
        
        ATTR_SYS --> RACE_MOD
        ATTR_SYS --> EVOL_MOD
        ATTR_SYS --> BUFF_MOD
        ATTR_SYS --> EQUIP_MOD
        
        RACE_MOD --> CALC
        EVOL_MOD --> CALC
        BUFF_MOD --> CALC
        EQUIP_MOD --> CALC
        
        VIT --> CALC
        STR --> CALC
        AGI --> CALC
        INT --> CALC
        RES --> CALC
        
        CALC --> FINAL
        CALC --> CACHE
    end
```

Modificadores podem vir de:

* Raça
* Evolução
* Buffs temporários

---

### 🔄 Evolução

Sistema baseado em **condições e comportamento do jogador**.

```mermaid
flowchart TD
    START[Raça Atual] --> COND{Verifica Condições}
    
    COND -->|Condições Atendidas| TRIGGER{Ação do Jogador}
    COND -->|Condições Não Atendidas| WAIT[Aguarda]
    
    TRIGGER -->|Uso de Magia| EVOL1[Evolui para Mago]
    TRIGGER -->|Combate Corpo a Corpo| EVOL2[Evolui para Guerreiro]
    TRIGGER -->|Exploração| EVOL3[Evolui para Explorador]
    TRIGGER -->|Outras Ações| EVOL4[Outras Evoluções]
    
    EVOL1 --> APPLY[Aplica Efeitos da Evolução]
    EVOL2 --> APPLY
    EVOL3 --> APPLY
    EVOL4 --> APPLY
    
    APPLY --> UPDATE[Atualiza Atributos]
    UPDATE --> NOTIFY[Notifica Jogador]
    NOTIFY --> SAVE[Salva Progresso]
    
    subgraph "Condições de Exemplo"
        C1[Intelecto > 100]
        C2[Nível > 20]
        C3[Tempo no Bioma > 10min]
        C4[Uso de Habilidade > 50x]
    end
    
    COND --> C1
    COND --> C2
    COND --> C3
    COND --> C4
```

Exemplos de gatilhos:

* Uso de habilidades
* Bioma
* Horário (dia/noite)
* Combate

**Exemplo:**

```json
{
  "from": "elf",
  "to": "high_elf",
  "conditions": ["intellect > 100", "level > 20"],
  "triggers": ["magic_usage"]
}
```

---

### ⚔️ Habilidades

Cada raça possui habilidades ativas:

* Cooldown
* Custo (energia, vida, etc)
* Efeitos

```mermaid
sequenceDiagram
    participant J as Jogador
    participant S as Sistema de Habilidades
    participant C as Cooldown Manager
    participant R as Resource Manager
    participant E as Effect System
    
    J->>S: Usa Habilidade "Dash"
    S->>C: Verifica Cooldown
    alt Cooldown Ativo
        C-->>J: "Habilidade em Cooldown"
    else Cooldown Disponível
        C->>R: Verifica Recursos
        alt Recursos Suficientes
            R->>R: Consome Recursos
            R->>E: Aplica Efeitos
            E->>J: Efeito Aplicado (Speed Boost)
            E->>C: Inicia Cooldown
            C-->>J: Habilidade Usada com Sucesso
        else Recursos Insuficientes
            R-->>J: "Recursos Insuficientes"
        end
    end
```

**Exemplo:**

```json
{
  "id": "dash",
  "cooldown": 5,
  "effects": ["speed_boost"]
}
```

---

### 👤 Dados do Jogador

Armazenados via NBT:

* Raça atual
* Progresso de evolução
* Atributos
* Cooldowns
* Habilidades desbloqueadas

```mermaid
graph TD
    subgraph "Armazenamento de Dados do Jogador"
        PLAYER[Jogador]
        
        subgraph "Dados NBT"
            NBT[Dados NBT]
            
            subgraph "Estrutura de Dados"
                RACE_DATA[Raça Atual]
                EVOL_DATA[Progresso de Evolução]
                ATTR_DATA[Atributos]
                CD_DATA[Cooldowns]
                ABIL_DATA[Habilidades Desbloqueadas]
                STATS[Estatísticas]
            end
        end
        
        subgraph "Sincronização"
            SYNC[Sincronização]
            CLIENT[Cliente]
            SERVER[Servidor]
            CACHE[Cache Local]
        end
        
        PLAYER --> NBT
        NBT --> RACE_DATA
        NBT --> EVOL_DATA
        NBT --> ATTR_DATA
        NBT --> CD_DATA
        NBT --> ABIL_DATA
        NBT --> STATS
        
        NBT --> SYNC
        SYNC --> CLIENT
        SYNC --> SERVER
        SYNC --> CACHE
    end
```

---

## 🌐 Networking

Sincronização simples usando Fabric:

* Atualização de atributos
* Uso de habilidades
* Mudanças de raça/evolução

```mermaid
graph LR
    subgraph "Arquitetura de Rede"
        CLIENT[Cliente]
        SERVER[Servidor]
        
        subgraph "Pacotes de Sincronização"
            ATTR_PKT[Pacote de Atributos]
            ABIL_PKT[Pacote de Habilidades]
            EVOL_PKT[Pacote de Evolução]
            RACE_PKT[Pacote de Raça]
        end
        
        subgraph "Estratégias de Sincronização"
            DELTA[Sincronização Delta]
            FULL[Sincronização Completa]
            EVENT[Sincronização por Evento]
        end
        
        CLIENT <--> SERVER
        SERVER --> ATTR_PKT
        SERVER --> ABIL_PKT
        SERVER --> EVOL_PKT
        SERVER --> RACE_PKT
        
        ATTR_PKT --> DELTA
        ABIL_PKT --> EVENT
        EVOL_PKT --> FULL
        RACE_PKT --> FULL
    end
```

Sem protocolos customizados complexos.

---

## 🗂️ Estrutura de Código

### Exemplo de organização

```mermaid
graph TD
    subgraph "Estrutura de Código Java"
        ROOT[src/main/java/dev/evoraces/]
        
        subgraph "Módulos Principais"
            RACE_MOD[race/]
            EVOL_MOD[evolution/]
            ABIL_MOD[ability/]
            ATTR_MOD[attribute/]
            PLAYER_MOD[player/]
        end
        
        subgraph "Exemplo: Módulo de Raças"
            RACE_FILE[Race.java]
            RACE_REG[RaceRegistry.java]
            RACE_MGR[RaceManager.java]
            RACE_DATA[RaceData.java]
            
            RACE_MOD --> RACE_FILE
            RACE_MOD --> RACE_REG
            RACE_MOD --> RACE_MGR
            RACE_MOD --> RACE_DATA
        end
        
        ROOT --> RACE_MOD
        ROOT --> EVOL_MOD
        ROOT --> ABIL_MOD
        ROOT --> ATTR_MOD
        ROOT --> PLAYER_MOD
    end
```

```
race/
├── Race.java
├── RaceRegistry.java

evolution/
├── EvolutionManager.java
├── EvolutionCondition.java

ability/
├── Ability.java
├── AbilityManager.java

attribute/
├── Attribute.java
├── AttributeManager.java

player/
├── PlayerData.java
├── PlayerDataManager.java
```

---

## 📦 Data-Driven Design

O mod é extensível via JSON:

* Novas raças → sem alterar código
* Novas habilidades → plugáveis via dados
* Evoluções → configuráveis

```mermaid
graph LR
    subgraph "Fluxo de Dados"
        JSON[JSON Config]
        LOADER[Loader]
        REGISTRY[Registry]
        GAME[Sistema do Jogo]
        
        subgraph "Tipos de Configuração"
            RACE_CFG[Config de Raças]
            EVOL_CFG[Config de Evoluções]
            ABIL_CFG[Config de Habilidades]
            ATTR_CFG[Config de Atributos]
        end
        
        JSON --> RACE_CFG
        JSON --> EVOL_CFG
        JSON --> ABIL_CFG
        JSON --> ATTR_CFG
        
        RACE_CFG --> LOADER
        EVOL_CFG --> LOADER
        ABIL_CFG --> LOADER
        ATTR_CFG --> LOADER
        
        LOADER --> REGISTRY
        REGISTRY --> GAME
    end
    
    subgraph "Vantagens"
        NO_CODE[Sem Alterar Código]
        MODULAR[Modular]
        USER_FRIENDLY[Amigável ao Usuário]
    end
    
    GAME --> NO_CODE
    GAME --> HOT_RELOAD
    GAME --> MODULAR
    GAME --> USER_FRIENDLY
```

---

## 🚀 Roadmap

### MVP (fase inicial)

* [ ] 3 raças
* [ ] sistema de atributos
* [ ] 1 evolução por raça
* [ ] 1 habilidade por raça

### Fase 2

* [ ] árvore de evolução
* [ ] perks/talentos
* [ ] balanceamento

### Fase 3

* [ ] integração com outros mods
* [ ] mais conteúdo
* [ ] otimizações

---

## ⚠️ Filosofia do Projeto

* Evitar overengineering
* Priorizar gameplay sobre arquitetura
* Manter código simples e legível
* Evoluir conforme necessidade real

---

## 🤝 Contribuição

Futuro suporte a:

* Novas raças via JSON
* Expansões modulares
* Integrações com outros mods

---

## 💡 Visão

Criar um sistema de raças que realmente impacte o jogo, onde:

> "a forma como você joga define no que você se torna."


## 📊 Resumo da Arquitetura Visual

```mermaid
graph TB
    subgraph "Visão Geral do Sistema"
        CONFIG[Configurações JSON]
        CORE[Sistema Core]
        PLAYER[Dados do Jogador]
        NET[Sincronização]
        UI[Interface]
        
        CONFIG --> CORE
        CORE --> PLAYER
        CORE --> NET
        CORE --> UI
        PLAYER --> NET
        NET --> UI
    end
    
    subgraph "Módulos do Sistema Core"
        RACE_SYS[Sistema de Raças]
        EVOL_SYS[Sistema de Evolução]
        ABIL_SYS[Sistema de Habilidades]
        ATTR_SYS[Sistema de Atributos]
        
        CORE --> RACE_SYS
        CORE --> EVOL_SYS
        CORE --> ABIL_SYS
        CORE --> ATTR_SYS
    end
    
    subgraph "Fluxo de Dados"
        JSON_LOAD[Carrega JSON]
        REGISTER[Registra no Sistema]
        UPDATE[Atualiza em Tempo Real]
        PERSIST[Persiste em NBT]
        SYNC_NET[Sincroniza na Rede]
        
        JSON_LOAD --> REGISTER
        REGISTER --> UPDATE
        UPDATE --> PERSIST
        PERSIST --> SYNC_NET
    end
    
    CONFIG --> JSON_LOAD
    JSON_LOAD --> CORE
    CORE --> REGISTER
    PLAYER --> PERSIST
    NET --> SYNC_NET
```
---

## 📝 Notas de Implementação

### **Performance**
- Cache de cálculos de atributos // analisar necessidade.
- Sincronização delta para reduzir tráfego de rede
- Carregamento lazy de configurações JSON

### **Extensibilidade**
- Todas as configurações via JSON
- API simples para adicionar novos sistemas
- Hot reload de configurações

### **Manutenibilidade**
- Código modular e bem separado
- Documentação visual com diagramas
- Testes unitários para cada módulo
