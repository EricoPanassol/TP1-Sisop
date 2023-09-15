# TP1 - Projeto de Execução Dinâmica de Processos

#### Sistemas Operacionais

O presente trabalho tem por objetivo explorar temas referentes ao escalonamento e troca entre processos que
utilizam um dado processador. É previsto o desenvolvimento de um ambiente que empregue uma política de
escalonamento específica, bem como gerencie a inclusão e remoção de processos que ocupam o processador. A carga
de processos deverá ser realizada a partir de programas que utilizarão uma linguagem assembly hipotética.

## Descrição de programas e características de execução e ocupação

O usuário deverá ser capaz de descrever pequenos programas a serem executados pelo ambiente. O ambiente
de execução é baseado em acumulador. Assim, para a execução de um programa, dois registradores estão presentes:
(i) o acumulador (acc) onde as operações são realizadas e (ii) o ponteiro da instrução em execução (pc). A linguagem
de programação hipotética a ser empregada pelo usuário para a programação e que manipula os dois registradores
descritos é apresentada na Tabela 1. Ali são definidos em quatro categorias, conforme listado na primeira coluna.

+------------+---------------+----------------------------+
| Categoria  | Mnemônico     | Função                     |
+------------+---------------+----------------------------+
| Aritmético |    ADD op1    |        acc=acc+(op1)       |
|            |    SUB op1    |        acc=acc-(op1)       |
|            |    MULT op1   |        acc=acc*(op1)       |
|            |    DIV op1    |        acc=acc/(op1)       |
+------------+---------------+----------------------------+
|   Memória  |    LOAD op1   |          acc=(op1)         |
|            |   STORE op1   |          (op1)=acc         |
+------------+---------------+----------------------------+
|    Salto   |  BRANY label  |         pc <- label        |
|            |  BRPOS label  | Se acc > 0 então pc <- op1 |
|            |  BRZERO label | Se acc = 0 então pc <- op1 |
|            |  BRNEG label  | Se acc < 0 então pc <- op1 |
|            |               |                            |
+------------+---------------+----------------------------+
|   Sistema  | SYSCALL index |     Chamada de sistema     |
+------------+---------------+----------------------------+