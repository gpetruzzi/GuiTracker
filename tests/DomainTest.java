import com.guitracker.app.Domain;
import java.time.*;
import java.util.*;
import java.io.*;
public class DomainTest {
 static int checks;
 static void check(boolean b){checks++;if(!b)throw new AssertionError("Check "+checks);}
 static void bad(Runnable r){checks++;try{r.run();}catch(Exception e){return;}throw new AssertionError("Aceptó un dato inválido");}
 static Map<String,Double> csv(String s){try{return Domain.parseCsv(new BufferedReader(new StringReader(s)),LocalDate.of(2026,10,23));}catch(IOException e){throw new RuntimeException(e);}}
 public static void main(String[] args){
  check(Domain.weight("75,4")==75.4);check(Domain.weight("20")==20);check(Domain.weight("500.0")==500);
  for(String s:new String[]{"NaN","Infinity","0","501","75.45",""})bad(()->Domain.weight(s));
  check(Domain.category(18.49).equals("Bajo peso"));check(Domain.category(18.5).equals("Peso normal"));check(Domain.category(25).equals("Sobrepeso"));check(Domain.category(30).equals("Obesidad"));
  ZonedDateTime mon=ZonedDateTime.of(2026,10,19,8,0,0,0,ZoneId.of("America/Argentina/Buenos_Aires"));
  check(Domain.nextReminder(mon,1,9,0).equals(mon.withHour(9)));
  check(Domain.nextReminder(mon.withHour(10),1,9,0).equals(mon.plusWeeks(1).withHour(9)));
  check(Domain.nextReminder(mon,0,9,0)==null);
  check(Domain.nextReminder(mon.withHour(10),5,9,0).equals(mon.plusDays(2).withHour(9)));
  check(Domain.nextReminder(mon.withHour(9),1,9,0).equals(mon.plusWeeks(1).withHour(9)));
  Map<String,Double> data=csv("\uFEFFfecha,peso_kg\r\n2026-10-22,75.4\r\n2026-10-22,75.2\r\n");check(data.size()==1);check(data.get("2026-10-22")==75.2);
  bad(()->csv("fecha,peso_kg\n2026-10-24,75.4"));bad(()->csv("fecha,peso_kg\n2026-02-30,75.4"));bad(()->csv("fecha,peso_kg\n2026-10-23,NaN"));bad(()->csv("fecha,peso_kg\n2026-10-23,75,4"));bad(()->csv("otra cabecera"));
  System.out.println(checks+" comprobaciones OK");
 }
}
