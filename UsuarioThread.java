///////////////////////// USUARIO /////////////////////////

import javax.xml.bind.SchemaOutputResolver;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


class UsuarioThread extends Thread {
    private int t_id;

    int usuario_id;
    int numAssentos;
    static Semaphore semaforo = null;
    static ArrayList<Integer> t_Assentos;
    static ArrayList<t_Assento> listaAssentos = null;
    static Queue<String> buffer = null;
    static BufferOut consumidor = null;

    public UsuarioThread(String usuario_id, Semaphore semaforo, ArrayList<Integer> t_Assentos, ArrayList<t_Assento> listaAssentos, int numAssentos, Queue<String> buffer, BufferOut consumidor){

        this.semaforo = semaforo;
        this.t_Assentos = t_Assentos;
        this.listaAssentos = listaAssentos;
        this.buffer = buffer;
        this.numAssentos = numAssentos;
        this.consumidor = consumidor;

        this.setName(usuario_id); //Seta o nome da thread como o numero de id passado por parametro
        this.usuario_id = Integer.parseInt(this.getName()); //Transforma essa id (que so pode ser passada como string) em int
    }

    public void visualizaAssentos(){

        buffer.add("Buffer: Visualiza");
        synchronized(consumidor){
          consumidor.notify();
        }

        System.out.println("Usuário " + usuario_id + "visulizou os assentos");
        for (int i = 0; i < t_Assentos.size(); i++) {
            System.out.print("[" + (t_Assentos.get(i)) + "]");
        }
        System.out.println("\n");
    }

    public int alocaAssentoLivre( int id) {
        // Inicio do bloco sincronizado, só pode entrar um thread.
        synchronized (UsuarioThread.class) {
            try {
                semaforo.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println(id + ": Usuario leu");


            //Escolhe tAssento aleatório até achar um vazio ou encontrar tudo reservado
            //Gera uma lista aleatoria do tamanho do mapa de assentos
            List<Integer> rndList = new ArrayList<Integer>(numAssentos);
            for (int i = 0; i < numAssentos; i++) {
                rndList.add(i);
            }
            Collections.shuffle(rndList);

            //Percorre cada item do mapa até achar um vazio, seguindo a ordem aleatória de rndlist
            for (int i = 0; i < rndList.size(); i++) {
                int j = rndList.get(i);
                //Se tiver vazio, aloca assento
                if (t_Assentos.get(j) == 0) {

                    buffer.add("Buffer: Aloca assento livre");
                    synchronized(consumidor){
                      consumidor.notify();
                    }

                    int numAssento = j + 1;
                    t_Assentos.set(j,id);
                    listaAssentos.get(j).setIdUsuario(id);
                    //listaAssentos.get(j).setNumAssento(numAssento);
                    System.out.println("\n Numero do Assento vazio: " + numAssento);
                    System.out.println("\n Usuario que reservou: " + listaAssentos.get(j).getIdUsuario());
                    System.out.println("\n Assento reservado: " + listaAssentos.get(j).getNumAssento());
                    System.out.println("\n Mapa de assentos:\n");
                    System.out.println(t_Assentos);
                    break;
                } else {
                    if (i == rndList.size() - 1) {
                        System.out.println("TUDO CHEIO DESISTE");//tudo cheio, sai
                        break;
                    } else {
                        // alocado, passa pro proximo
                        continue;
                    }
                }
            }
            semaforo.release();
            return 0;
        }
    }

    public int alocaAssentoDado(t_Assento assento, int id){
        synchronized (UsuarioThread.class) {
            try {
                semaforo.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (assento.getIdUsuario() == 0){

                buffer.add("Buffer: Aloca assento dado");
                synchronized(consumidor){
                  consumidor.notify();
                }

                assento.setIdUsuario(id);
                t_Assentos.set(assento.getNumAssento()-1, id);
                System.out.println("Assento " + assento.getNumAssento() + " alocado para o usuário "+assento.getIdUsuario());
            }
            else{
                System.out.println("Desculpe, usuário " +id + ", mas o assento já esta alocado pelo usuário " + assento.getIdUsuario());
            }

        }
        semaforo.release();
        return 0;
    }

    public int liberaAssento(t_Assento assento, int id){
        synchronized (UsuarioThread.class) {
            try {
                semaforo.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (assento.getIdUsuario() == id){

                buffer.add("Buffer: Libera assento");
                synchronized(consumidor){
                  consumidor.notify();
                }

                //É o mesmo usuário, pode liberar
                assento.setIdUsuario(0);
                t_Assentos.set(assento.getNumAssento()-1, 0);
                System.out.println("Assento "+assento.getNumAssento()+ " liberado pelo usuario "+id);
            }
            else{
                System.out.println("Assento " +assento.getNumAssento()+ " já se encontra liberado ou ocupado por outro usuário");
            }
            semaforo.release();
            return 0;
        }
    }
    public void run() {

        //alocaAssentoLivre(, 40);
       // for (int i = 0; i<1; i++){
        alocaAssentoDado(listaAssentos.get(3), usuario_id);
        liberaAssento(listaAssentos.get(3), usuario_id);
        System.out.println(t_Assentos);
        //}

        synchronized(consumidor){
          consumidor.notify(); //Da um ultimo notify se estiver faltando gente pra consumir
          consumidor.interrupt(); //Interrompe a thread consumidora pra terminar a execucao
        }
    }

}
