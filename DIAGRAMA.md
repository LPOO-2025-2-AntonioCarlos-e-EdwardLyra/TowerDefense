````mermaid
classDiagram
    direction TB
%% --- INTERFACES ---
class Drawable {
<<Interface>>
+draw(Graphics2D g2)
}

%% --- EXCEÇÕES ---
class SaldoInsuficienteException {
<<Exception>>
+SaldoInsuficienteException(String mensagem)
}

%% --- CLASSES PRINCIPAIS ---
class Main {
+static main(String[])
}

class GamePanel {
<<JPanel>>
<<Runnable>>
-int life
-int coins
-GameState gameState
-List~Enemy~ enemies
-List~Tower~ towers
+startGameThread()
+run()
+update()
+paintComponent(Graphics)
-handleMouse()
-gastarMoedas(int) void
-canPlaceTower(int, int) boolean
}

%% --- INPUTS ---
class KeyHandler {
<<KeyListener>>
+boolean upPressed
+boolean downPressed
}

class MouseHandler {
<<MouseAdapter>>
+int mouseX
+int mouseY
+boolean leftClicked
}

%% --- GERENCIADORES ---
class MapManager {
-int[][] mapLayout
+draw(Graphics2D)
+isPlaceable(int, int)
}

class RoundManager {
-int currentRound
-int[] enemiesPerRound
-long spawnDelay
+update()
-startNextRound()
-spawnEnemy(int enemyType)
}

%% --- ENTIDADES DO JOGO (DEFESA) ---
class Tower {
-TowerType type
-int level
-List~Projectile~ projectiles
+int cost
+upgrade()
+update(List~Enemy~)
+draw(Graphics2D)
-findTarget(List~Enemy~) Enemy
}

class Projectile {
-Enemy target
-boolean appliesSlow
+update()
+draw(Graphics2D)
}

%% --- ENTIDADES DO JOGO (INIMIGOS) ---
class Enemy {
<<Abstract>>
#int speed
+int health
+boolean active
#List~Point~ path
+update()
+takeDamage(int)
+applySlow(long)
+draw(Graphics2D)
+abstract getColor() Color
}

class Javali {
+getColor() Color
}

class Leitao {
+getColor() Color
}

class JavaliAlfa {
+getColor() Color
}

%% --- ENUMS ---
class GameState {
<<Enumeration>>
NORMAL
PLACING_TOWER
GAME_OVER
    }

class TowerType {
<<Enumeration>>
NORMAL
SNIPER
}

%% --- RELACIONAMENTOS ---

%% Implementação de Interface
MapManager ..|> Drawable
Tower ..|> Drawable
Enemy ..|> Drawable
Projectile ..|> Drawable

%% Estrutura Principal
Main ..> GamePanel : cria
GamePanel "1" *-- "1" KeyHandler
GamePanel "1" *-- "1" MouseHandler
GamePanel "1" *-- "1" MapManager
GamePanel "1" *-- "1" RoundManager

%% Exceções
GamePanel ..> SaldoInsuficienteException : lança

%% Listas e Conteúdo
GamePanel "1" o-- "0..*" Enemy : lista
GamePanel "1" o-- "0..*" Tower : lista

%% Torres e Projéteis
Tower "1" *-- "0..*" Projectile : possui
Tower ..> TowerType : usa
Projectile --> "1" Enemy : persegue

%% Herança de Inimigos
Javali --|> Enemy
Leitao --|> Enemy
JavaliAlfa --|> Enemy

%% Lógica de Rounds (Factory Method)
RoundManager ..> Javali : cria
RoundManager ..> Leitao : cria
RoundManager ..> JavaliAlfa : cria

%% Estados
GamePanel ..> GameState : usa
````
