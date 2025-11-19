````mermaid
classDiagram
    direction TB

    %% --- INTERFACES ---
    class Drawable {
        <<Interface>>
        +draw(Graphics2D)
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
        -canPlaceTower(int, int)
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
        +update()
        -startNextRound()
    }

    %% --- ENTIDADES DO JOGO ---
    class Tower {
        -TowerType type
        -int level
        -List~Projectile~ projectiles
        +int cost
        +upgrade()
        +update(List~Enemy~)
        +draw(Graphics2D)
    }

    class Projectile {
        -Enemy target
        -boolean appliesSlow
        +update()
        +draw(Graphics2D)
    }

    class Enemy {
        -int type
        +int health
        +int speed
        +update()
        +takeDamage(int)
        +applySlow(long)
        +draw(Graphics2D)
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

    %% Implementação de Interface (O que tu já fizeste!)
    MapManager ..|> Drawable
    Tower ..|> Drawable
    Enemy ..|> Drawable
    Projectile ..|> Drawable

    %% Main e GamePanel
    Main ..> GamePanel : cria
    GamePanel "1" o-- "1" KeyHandler
    GamePanel "1" o-- "1" MouseHandler
    GamePanel "1" o-- "1" MapManager
    GamePanel "1" o-- "1" RoundManager
    
    %% Listas e Conteúdo
    GamePanel "1" o-- "0..*" Enemy : lista
    GamePanel "1" o-- "0..*" Tower : lista

    %% Associações Internas
    Tower "1" *-- "0..*" Projectile : cria
    Projectile "1" --> "1" Enemy : persegue
    RoundManager ..> Enemy : instancia
    
    %% Enums
    GamePanel ..> GameState
    Tower ..> TowerType
````
