# 📝 Guia de Commits — EvoRaces

## 🎯 Objetivo

* Padronizar commits
* Melhorar leitura do histórico
* Permitir automação (changelog, CI)

---

## 📋 Padrão (Conventional Commits)

Formato:

```
<tipo>(<escopo>): <descrição curta>

[corpo opcional]

[rodapé opcional]
```

### Tipos

| Tipo       | Uso                                   |
| ---------- | ------------------------------------- |
| `feat`     | nova funcionalidade                   |
| `fix`      | correção de bug                       |
| `docs`     | documentação                          |
| `style`    | formatação                            |
| `refactor` | refatoração sem alterar comportamento |
| `perf`     | melhoria de performance               |
| `test`     | testes                                |
| `build`    | build/dependências                    |
| `ci`       | CI/CD                                 |
| `chore`    | manutenção                            |
| `revert`   | reversão                              |

---

## 📁 Escopos do Projeto

| Escopo      | Uso              |
| ----------- | ---------------- |
| `race`      | raças            |
| `evolution` | evolução         |
| `ability`   | habilidades      |
| `attribute` | atributos        |
| `player`    | dados do jogador |
| `network`   | sincronização    |
| `ui`        | interface        |
| `config`    | configuração     |
| `build`     | build            |
| `docs`      | documentação     |

---

## ✍️ Exemplos

```bash
feat(race): adiciona raça vampiro
fix(evolution): corrige trigger noturno
refactor(attribute): extrai cálculo de atributos
perf(player): otimiza cache
docs: atualiza README
```

### Com corpo

```bash
feat(evolution): implementa evolução por bioma

- adiciona detecção de bioma
- cria sistema de progresso
- adiciona efeitos visuais
```

### Breaking change

```bash
feat(api)!: altera registro de raças

BREAKING CHANGE: registerRace agora requer RaceConfig
```

---

## 🔄 Workflow recomendado

```bash
git add .
git diff --cached
git diff --cached > changes.patch   # opcional (IA)
git commit -m "tipo(escopo): descrição"
```

👉 Use o `.patch` quando quiser gerar commit com IA.

---

## 🤖 Uso de IA (mantido, mas direto)

```
Gere uma mensagem de commit em português e a branch em inglês no padrão Conventional Commits.

Formato: <tipo>(<escopo>): <descrição>

Tipos: feat, fix, docs, style, refactor, perf, test, build, ci, chore, revert
Escopos: race, evolution, ability, attribute, player, network, ui, config

Regras:
- até 72 caracteres
- verbo no presente (adiciona, corrige, melhora)
- seja específico
- incluir escopo quando fizer sentido

Diff:
[cole git diff --cached]
```

---

## 📌 Boas práticas

**Faça:**

* commits pequenos e claros
* use escopo quando relevante
* revise com `git diff --cached`

**Evite:**

* `update`, `fix bug`, `wip`
* commits grandes demais

---

## 🔍 Troubleshooting

```bash
# corrigir último commit
git commit --amend -m "mensagem correta"

# adicionar arquivo esquecido
git add arquivo
git commit --amend --no-edit

# dividir commit
git reset HEAD~1
```

---

## 🚀 Resumo rápido

```bash
Formato:
tipo(escopo): descrição

Fluxo:
git add .
git diff --cached
git commit -m "..."

IA (opcional):
git diff --cached > changes.patch
```

---
