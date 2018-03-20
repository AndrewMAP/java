import java.util.*;
import java.util.LinkedList;
import java.util.Queue;

public class BufferOut extends Thread{

  static Queue<String> buffer = null;
  private String linha;

  public BufferOut(Queue<String> buffer){
    this.buffer = buffer;
  }

  synchronized void escreveArquivo(){
    try{
      linha = buffer.remove();
    }catch (NoSuchElementException n){
      //Ignora
    }
    System.out.println(linha);
  }

  public void run() {
    while(!isInterrupted()){
      try {
        synchronized(this){
          wait(); //Esperando chegar produto
      }
      }catch (InterruptedException e) {
        e.printStackTrace();
      }
      //Sai do wait e consome produto
      escreveArquivo();
    }
  }
}
