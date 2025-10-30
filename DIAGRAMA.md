```mermaid
classDiagram
    direction TB

    class Main {
        +static main(String[])
    }

    class GamePanel {
        <<JPanel>>
        <<Runnable>>
        -int life
        -int coins
        -GameState gameState
        -KeyHandler keyH
        -MouseHandler mouseH
        -MapManager mapManager
        -RoundManager roundManager
        -List~Enemy~ enemies
        -List~Tower~ towers
        +startGameThread()
        +run()
        +update()
        +paintComponent(Graphics)
        -handleMouse()
        -canPlaceTower(int, int)
        -removeTowerAt(int, int)
    }

    class KeyHandler {
        <<KeyListener>>
        +boolean upPressed
        +boolean downPressed
        +boolean leftPressed
        +boolean rightPressed
        +keyPressed(KeyEvent)
        +keyReleased(KeyEvent)
    }

    class MouseHandler {
        <<MouseAdapter>>
        +int mouseX
        +int mouseY
        +boolean leftClicked
        +boolean rightClicked
        +mousePressed(MouseEvent)
        +mouseMoved(MouseEvent)
        +mouseDragged(MouseEvent)
        +resetClicks()
    }

    class MapManager {
        -GamePanel gp
        -int[][] mapLayout
        -Color PATH_COLOR
        +MapManager(GamePanel)
        +draw(Graphics2D)
        +isPlaceable(int, int)
    }

    class RoundManager {
        -GamePanel gp
        -int currentRound
        -int[] enemiesPerRound
        -boolean roundInProgress
        +RoundManager(GamePanel)
        +update()
        -startNextRound()
        +checkRoundCompletion()
        +getCurrentRound()
    }

    class Tower {
        -GamePanel gp
        -TowerType type
        -List~Projectile~ projectiles
        +int cost
        -int attackRange
        -long fireRate
        -int damage
        +Tower(GamePanel, int, int, TowerType)
        +update(List~Enemy~)
        -findTarget(List~Enemy~)
        +draw(Graphics2D)
    }

    class Enemy {
        -GamePanel gp
        +int x, y
        +int health
        -int speed
        +boolean active
        -List~Point~ path
        +Enemy(GamePanel)
        +update()
        +takeDamage(int)
        +draw(Graphics2D)
    }

    class Projectile {
        -GamePanel gp
        -Enemy target
        -int speed
        -int damage
        +boolean active
        +Projectile(GamePanel, int, int, Enemy, int)
        +update()
        +draw(Graphics2D)
    }

    enum GameState {
        NORMAL
        PLACING_TOWER_NORMAL
        PLACING_TOWER_SNIPER
        GAME_OVER
    }

    enum TowerType {
        NORMAL
        SNIPER
    }

    ' --- Relacionamentos ---

    ' Main cria GamePanel
    Main ..> GamePanel : cria

    ' GamePanel (Agregação/Composição)
    GamePanel "1" o-- "1" KeyHandler
    GamePanel "1" o-- "1" MouseHandler
    GamePanel "1" o-- "1" MapManager
    GamePanel "1" o-- "1" RoundManager
    GamePanel "1" o-- "0..*" Enemy : contém
    GamePanel "1" o-- "0..*" Tower : contém

    ' Enums Aninhados (Composição)
    GamePanel +-- GameState
    Tower +-- TowerType

    ' Associações (Outras classes referenciam GamePanel)
    MapManager --> GamePanel : "1"
    RoundManager --> GamePanel : "1"
    Tower --> GamePanel : "1"
    Enemy --> GamePanel : "1"
    Projectile --> GamePanel : "1"

    ' Tower cria Projectiles (Agregação)
    Tower "1" o-- "0..*" Projectile : cria

    ' RoundManager cria Enemies (Dependência)
    RoundManager ..> Enemy : cria

    ' Projectile mira em um Enemy (Associação)
    Projectile "1" --> "1" Enemy : mira

    ' Tower procura Enemies (Dependência)
    Tower ..> Enemy : usa

    ' Herança / Implementação
    GamePanel ..|> Runnable
    'GamePanel --|> JPanel (Implícito pela GUI)
    MouseHandler --|> MouseAdapter
    KeyHandler ..|> KeyListener
```
