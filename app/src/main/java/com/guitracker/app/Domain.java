// SPDX-License-Identifier: GPL-3.0-only
package com.guitracker.app;
import java.time.*;
import java.util.*;

/** Reglas de datos, independientes de la interfaz Android. */
public final class Domain {
 private Domain() {}
 public static double weight(String text) {
  double v=Double.parseDouble(text.trim().replace(',','.'));
  if(!Double.isFinite(v)||v<20||v>500||Math.abs(v*10-Math.rint(v*10))>0.00001)throw new IllegalArgumentException("Peso inválido");
  return v;
 }
 public static String category(double bmi) {
  return bmi<18.5?"Bajo peso":bmi<25?"Peso normal":bmi<30?"Sobrepeso":"Obesidad";
 }
 public static ZonedDateTime nextReminder(ZonedDateTime now,int mask,int hour,int minute) {
  if(mask==0)return null;
  for(int i=0;i<=7;i++){
   ZonedDateTime at=now.plusDays(i).withHour(hour).withMinute(minute).withSecond(0).withNano(0);
   if(at.isAfter(now)&&(mask&(1<<(at.getDayOfWeek().getValue()-1)))!=0)return at;
  }
  throw new IllegalArgumentException("Días inválidos");
 }
 public static Map<String,Double> parseCsv(java.io.BufferedReader reader,LocalDate today) throws java.io.IOException {
  String header=reader.readLine();
  if(header==null||!header.replace("\uFEFF","").equals("fecha,peso_kg"))throw new IllegalArgumentException("Cabecera inválida");
  Map<String,Double> out=new TreeMap<>();String line;int count=0;
  while((line=reader.readLine())!=null){
   if(line.trim().isEmpty())continue;
   if(++count>50000)throw new IllegalArgumentException("Archivo demasiado grande");
   String[] cells=line.split(",",-1);
   if(cells.length!=2)throw new IllegalArgumentException("Columnas inválidas");
   LocalDate day=LocalDate.parse(cells[0]);
   if(day.isAfter(today)||day.getYear()<1900)throw new IllegalArgumentException("Fecha inválida");
   out.put(day.toString(),weight(cells[1]));
  }
  return out;
 }
}
