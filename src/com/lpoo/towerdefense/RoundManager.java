package com.lpoo.towerdefense;

public class RoundManager {

    private final GamePanel gp; // Ao adicionar final nas variáveis elas tornam-se constantes(Elas são valores que você define uma vez e nunca mais muda)

    // Configuração dos rounds
    private int currentRound = 0;
    private final int[] enemiesPerRound = {0, 3, 5, 8, 16, 18}; // Round 0 é nulo
    private final long spawnDelay = 1500; // 1.5 segundos entre cada spawn
    private final long timeBetweenRounds = 5000; // 5 segundos de pausa entre rounds

    // Controle de estado
    private long lastSpawnTime = 0;
    private int enemiesSpawnedThisRound = 0;
    private boolean roundInProgress = false;
    private long roundEndTime = 0;
    private boolean allRoundsCompleted = false;


    //Construtor do gerenciador de rounds
    public RoundManager(GamePanel gp) {
        this.gp = gp;
    }


    // Inicia o próximo round, incrementando o contador e resetando os spawns.
    private void startNextRound() {
        currentRound++;
        if (currentRound < enemiesPerRound.length) {
            enemiesSpawnedThisRound = 0;
            roundInProgress = true;
            System.out.println("Starting Round " + currentRound);
        } else {
            System.out.println("All rounds completed!");
            allRoundsCompleted = true; // Ativa a trava de fim de jogo
            currentRound = enemiesPerRound.length - 1; // Trava o contador no último round
        }
    }

    //Controla o tempo para iniciar rounds e spawnar inimigos
    public void update() {
        if (allRoundsCompleted) {
            return; // Não faz nada se todos os rounds acabaram
        }

        // Lógica de tempo entre rounds
        if (!roundInProgress) {
            if (System.currentTimeMillis() - roundEndTime > timeBetweenRounds) {
                startNextRound();
            }
        }
        // Lógica de tempo durante um round (spawn de inimigos)
        else {
            int enemiesToSpawn = enemiesPerRound[currentRound];
            if (enemiesSpawnedThisRound < enemiesToSpawn && System.currentTimeMillis() - lastSpawnTime > spawnDelay) {
                // Usa a referência 'gp' para adicionar inimigos na lista do GamePanel
                gp.enemies.add(new Enemy(gp));
                enemiesSpawnedThisRound++;
                lastSpawnTime = System.currentTimeMillis();
            }
        }
    }

    // Verifica se um round em progresso terminou
    public void checkRoundCompletion() {
        if (!roundInProgress) {
            return; // Round já terminou ou nem começou
        }

        int enemiesThisRound = enemiesPerRound[currentRound];

        if (enemiesSpawnedThisRound == enemiesThisRound && gp.enemies.isEmpty()) {
            roundInProgress = false;
            roundEndTime = System.currentTimeMillis(); // Inicia o timer para o próximo round
            System.out.println("Round " + currentRound + " completed!");
        }
    }

    // Retorna o número do round atual
    public int getCurrentRound() {
        return currentRound;
    }
}