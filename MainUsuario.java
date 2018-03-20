///////////////////////// MAIN /////////////////////////
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.Queue;


public class Main {
  static Semaphore semaforo = new Semaphore(1, true);
  static int numAssentos = 10;
  static ArrayList<Integer> t_Assentos = new ArrayList<Integer>(Collections.nCopies(numAssentos,0));
  static ArrayList<t_Assento> listaAssentos = new ArrayList<t_Assento>(numAssentos);
  static Queue<String> buffer = new LinkedList<String>();
  //buffer.add(); buffer.remove();

  public static void main(String[] args){
    BufferOut consumidor = new BufferOut(buffer);

    UsuarioThread usuario1 = new UsuarioThread("1", semaforo, t_Assentos, listaAssentos, numAssentos, buffer, consumidor);
    //UsuarioThread usuario2 = new UsuarioThread("2", semaforo, t_Assentos, listaAssentos, numAssentos);
    //UsuarioThread usuario3 = new UsuarioThread("3", semaforo, t_Assentos, listaAssentos, numAssentos);
    //UsuarioThread usuario4 = new UsuarioThread("4", semaforo, t_Assentos, listaAssentos, numAssentos);



      System.out.println("Cavalao: " + t_Assentos.get(1));
    for (int i=0; i<10;i++){
      t_Assento assento = new t_Assento();
      assento.setNumAssento(i+1);
      listaAssentos.add(assento);
    }
    //usuario1.visualizaAssentos();
    //usuario1.escrever();


    //usuario2.alocaAssentoLivre(listaAssentos.get(0), usuario2.usuario_id);
      usuario1.start();
      consumidor.start();
      //usuario2.start();
     // usuario3.start();
      //usuario4.start();
  }
}
