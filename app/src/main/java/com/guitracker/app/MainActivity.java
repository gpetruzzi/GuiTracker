// SPDX-License-Identifier: GPL-3.0-only
package com.guitracker.app;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.*;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class MainActivity extends Activity {
 int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);} boolean darkMode=false; boolean viewingHistory=false; JSONObject data; LinearLayout root,body; int ink,muted,card,bg; LocalDate end=LocalDate.now();
 static final int ACCENT=0xff219c80; static final String FILE="guitracker.json";
 static JSONObject read(Context c) { try(InputStream in=new android.util.AtomicFile(new File(c.getFilesDir(),FILE)).openRead()){ByteArrayOutputStream b=new ByteArrayOutputStream();byte[] a=new byte[4096];int n;while((n=in.read(a))!=-1)b.write(a,0,n);return new JSONObject(b.toString("UTF-8"));}catch(FileNotFoundException e){return new JSONObject();}catch(Exception e){throw new IllegalStateException("No se pudieron leer los datos guardados",e);} }
 void save() {try{android.util.AtomicFile f=new android.util.AtomicFile(new File(getFilesDir(),FILE));FileOutputStream out=f.startWrite();try{out.write(data.toString().getBytes("UTF-8"));f.finishWrite(out);}catch(Exception e){f.failWrite(out);throw e;}}catch(Exception e){throw new IllegalStateException(e);} }
 JSONObject records(){JSONObject r=data.optJSONObject("records");if(r==null){r=new JSONObject();try{data.put("records",r);}catch(Exception e){throw new RuntimeException(e);}}return r;}
 void put(String k,Object v){try{data.put(k,v);}catch(Exception e){throw new RuntimeException(e);}}
 @Override public void onCreate(Bundle b){super.onCreate(b);boolean dark=(getResources().getConfiguration().uiMode&Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES;darkMode=dark;setTheme(dark?android.R.style.Theme_Material_NoActionBar:android.R.style.Theme_Material_Light_NoActionBar);ink=dark?0xffedf5f2:0xff183c34;muted=dark?0xff9bb3aa:0xff73867e;card=dark?0xff192e27:0xffffffff;bg=dark?0xff101e19:0xfff3f6f3;try{data=read(this);}catch(Exception e){new AlertDialog.Builder(this).setMessage(e.getMessage()).setPositiveButton("Cerrar",(d,w)->finish()).setCancelable(false).show();return;}if(Build.VERSION.SDK_INT>=33)getOnBackInvokedDispatcher().registerOnBackInvokedCallback(0,()->{if(viewingHistory){viewingHistory=false;show();}else finish();});show();schedule(this);if(!data.has("name"))profile();}
 TextView text(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setPadding(0,8,0,8);return t;}
 void title(String s){body.addView(text(s,22,ink));}
 Button button(String s,Runnable r){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setTextSize(14);b.setTextColor(ink);b.setTypeface(Typeface.create("sans-serif-medium",0));b.setBackground(new android.graphics.drawable.RippleDrawable(android.content.res.ColorStateList.valueOf(0x20298d6b),rounded(darkMode?0xff233d32:0xfff1f6f2,14),null));b.setOnClickListener(v->r.run());return b;}
 void rowButtons(String a,Runnable ar,String b,Runnable br){LinearLayout l=new LinearLayout(this);l.addView(button(a,ar),new LinearLayout.LayoutParams(0,-2,1));l.addView(button(b,br),new LinearLayout.LayoutParams(0,-2,1));body.addView(l);}
 ArrayList<String> dates(){ArrayList<String>a=new ArrayList<>();records().keys().forEachRemaining(a::add);Collections.sort(a);return a;}
 String num(double v){return String.format(new Locale("es","AR"),"%.1f",v);}
 String date(LocalDate d){return d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));}
 android.graphics.drawable.GradientDrawable rounded(int color,int radius){
  android.graphics.drawable.GradientDrawable d=new android.graphics.drawable.GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;
 }
 TextView label(String value,int size,int color,String font){TextView t=text(value,size,color);t.setPadding(0,0,0,0);t.setFontFeatureSettings("tnum");t.setTypeface(Typeface.create(font,Typeface.NORMAL));return t;}
 void space(LinearLayout parent,int height){parent.addView(new View(this),new LinearLayout.LayoutParams(1,dp(height)));}
 LinearLayout panel(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);l.setPadding(dp(18),dp(18),dp(18),dp(18));l.setBackground(rounded(card,24));return l;}
 void addPanel(LinearLayout l){body.addView(l,new LinearLayout.LayoutParams(-1,-2));space(body,14);}
 void show(){
  root=new LinearLayout(this);root.setOrientation(1);root.setBackgroundColor(bg);
  root.setPadding(dp(20),dp(8),dp(20),dp(8));
  root.setOnApplyWindowInsetsListener((v,in)->{v.setPadding(dp(20),in.getSystemWindowInsetTop()+dp(8),dp(20),in.getSystemWindowInsetBottom()+dp(8));return in;});
  setContentView(root);
  LinearLayout header=new LinearLayout(this);header.setGravity(Gravity.CENTER_VERTICAL);
  if(viewingHistory)header.addView(smallButton("←","Volver al inicio",()->{viewingHistory=false;show();}),new LinearLayout.LayoutParams(dp(48),dp(48)));
  else {TextView mark=label("G",20,0xffeffff6,"sans-serif-medium");mark.setGravity(Gravity.CENTER);mark.setBackground(rounded(0xff245e4b,12));LinearLayout.LayoutParams markParams=new LinearLayout.LayoutParams(dp(36),dp(36));markParams.setMarginEnd(dp(10));header.addView(mark,markParams);}
  TextView brand=label(viewingHistory?"Historial":"GuiTracker",20,ink,"sans-serif-medium");brand.setLetterSpacing(-0.025f);header.addView(brand,new LinearLayout.LayoutParams(0,-2,1));
  Button menu=smallButton("⋮","Abrir menú",()->{});menu.setOnClickListener(v->openMenu(menu));header.addView(menu,new LinearLayout.LayoutParams(dp(48),dp(48)));root.addView(header);
  ScrollView scroll=new ScrollView(this);scroll.setClipToPadding(false);scroll.setVerticalScrollBarEnabled(false);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
  body=new LinearLayout(this);body.setOrientation(1);body.setPadding(0,dp(18),0,dp(18));scroll.addView(body);
  if(viewingHistory){showHistory();return;}
  TextView greeting=label(data.has("name")?"Hola, "+data.optString("name"):"Tu progreso empieza acá",26,ink,"sans-serif-medium");greeting.setLetterSpacing(-0.035f);body.addView(greeting);space(body,5);
  body.addView(label("Un día a la vez. A tu ritmo.",14,muted,"sans-serif"));space(body,22);
  ArrayList<String> ds=dates();double last=ds.isEmpty()?0:records().optDouble(ds.get(ds.size()-1));
  double height=data.optDouble("height",0)/100,goal=data.optDouble("goal",0);
  LinearLayout hero=panel();hero.setPadding(dp(22),dp(22),dp(22),dp(22));
  android.graphics.drawable.GradientDrawable gradient=new android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.TL_BR,new int[]{0xff163f32,0xff28775a});gradient.setCornerRadius(dp(26));hero.setBackground(gradient);
  TextView overline=label("ÚLTIMO PESAJE",11,0xffbdd7c8,"sans-serif-medium");overline.setLetterSpacing(0.14f);hero.addView(overline);space(hero,10);
  LinearLayout weightRow=new LinearLayout(this);weightRow.setGravity(Gravity.BOTTOM);
  TextView weight=label(last>0?num(last):"—",52,0xffffffff,"sans-serif-light");weight.setLetterSpacing(-0.06f);weightRow.addView(weight);
  TextView unit=label("kg",21,0xffc3decd,"sans-serif");unit.setPadding(dp(10),0,0,dp(9));weightRow.addView(unit);hero.addView(weightRow);space(hero,12);
  hero.addView(label(last>0?"Registrado el "+date(LocalDate.parse(ds.get(ds.size()-1))):"Tu primer registro empieza hoy.",13,0xffd5e8dc,"sans-serif"));addPanel(hero);
  LinearLayout metrics=new LinearLayout(this);metrics.setBaselineAligned(false);
  LinearLayout bmiCard=panel(),goalCard=panel();
  bmiCard.addView(label("ÍNDICE DE MASA CORPORAL",9,muted,"sans-serif-medium"));space(bmiCard,10);
  bmiCard.addView(label(last>0&&height>0?num(last/(height*height)):"—",30,ink,"sans-serif-medium"));space(bmiCard,7);
  bmiCard.addView(label(last>0&&height>0?Domain.category(last/(height*height)):"IMC para adultos",12,muted,"sans-serif"));
  goalCard.addView(label("TU META",10,muted,"sans-serif-medium"));space(goalCard,10);
  goalCard.addView(label(goal>0?num(goal)+" kg":"—",27,ink,"sans-serif-medium"));space(goalCard,7);
  goalCard.addView(label(goal>0&&last>0?"Diferencia: "+num(Math.abs(last-goal))+" kg":goal>0?"Tu peso deseado":"Definila en tu perfil",12,muted,"sans-serif"));
  LinearLayout.LayoutParams metricParams=new LinearLayout.LayoutParams(0,-1,1);metricParams.setMarginEnd(dp(10));metrics.addView(bmiCard,metricParams);metrics.addView(goalCard,new LinearLayout.LayoutParams(0,-1,1));body.addView(metrics);space(body,12);
  TextView note=label("El IMC se calcula e interpreta para personas adultas. Es orientativo y no constituye un diagnóstico.",11,muted,"sans-serif");note.setLineSpacing(dp(3),1);note.setPadding(dp(4),0,dp(4),0);body.addView(note);space(body,22);
  LinearLayout chartCard=panel();chartCard.addView(label("Tu evolución",20,ink,"sans-serif-medium"));space(chartCard,6);
  chartCard.addView(label(date(end.minusDays(364))+" — "+date(end),12,muted,"sans-serif"));space(chartCard,14);
  LinearLayout legend=new LinearLayout(this);TextView weightLegend=label("●  Peso",11,darkMode?0xff67d6ad:0xff248467,"sans-serif-medium");legend.addView(weightLegend);
  if(goal>0){TextView goalLegend=label("  ┄  Meta",11,darkMode?0xffe4be76:0xffab7c30,"sans-serif-medium");goalLegend.setPadding(dp(14),0,0,0);legend.addView(goalLegend);}chartCard.addView(legend);
  chartCard.addView(new Chart(),new LinearLayout.LayoutParams(-1,dp(215)));
  LinearLayout navigation=new LinearLayout(this);navigation.setGravity(Gravity.CENTER_VERTICAL);
  navigation.addView(smallButton("‹","Año anterior",()->{end=end.minusDays(365);show();}),new LinearLayout.LayoutParams(dp(48),dp(48)));
  TextView period=label("365 días",12,muted,"sans-serif-medium");period.setGravity(Gravity.CENTER);navigation.addView(period,new LinearLayout.LayoutParams(0,-2,1));
  Button next=smallButton("›","Año siguiente",()->{end=end.plusDays(365);if(end.isAfter(LocalDate.now()))end=LocalDate.now();show();});next.setEnabled(end.isBefore(LocalDate.now()));next.setAlpha(next.isEnabled()?1f:0.25f);navigation.addView(next,new LinearLayout.LayoutParams(dp(48),dp(48)));chartCard.addView(navigation);addPanel(chartCard);
  Button register=button("＋  Registrar peso",()->measurement(LocalDate.now()));register.setTextColor(0xffffffff);register.setTextSize(16);register.setTypeface(Typeface.create("sans-serif-medium",Typeface.NORMAL));register.setStateListAnimator(null);register.setElevation(dp(2));
  register.setBackground(new android.graphics.drawable.RippleDrawable(android.content.res.ColorStateList.valueOf(0x33ffffff),rounded(darkMode?0xff298d6b:0xff206e53,18),null));body.addView(register,new LinearLayout.LayoutParams(-1,dp(58)));
 }
 Button smallButton(String symbol,String description,Runnable action){
  Button b=button(symbol,action);b.setContentDescription(description);b.setTextSize(24);b.setTextColor(ink);
  b.setMinWidth(0);b.setMinimumWidth(0);b.setPadding(0,0,0,0);
  android.util.TypedValue background=new android.util.TypedValue();getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless,background,true);b.setBackgroundResource(background.resourceId);return b;
 }
 void decorateDialog(AlertDialog dialog){
  if(dialog.getWindow()!=null)dialog.getWindow().setBackgroundDrawable(rounded(card,26));
  for(int id:new int[]{-1,-2,-3}){Button b=dialog.getButton(id);if(b!=null){b.setAllCaps(false);b.setTextColor(darkMode?0xff67d6ad:0xff206e53);b.setTypeface(Typeface.create("sans-serif-medium",0));}}
 }
 AlertDialog sheet(String title,LinearLayout content){
  ScrollView scroll=new ScrollView(this);scroll.setFillViewport(false);scroll.addView(content);
  AlertDialog dialog=new AlertDialog.Builder(this).setTitle(title).setView(scroll).setNegativeButton("Cerrar",null).create();dialog.show();decorateDialog(dialog);return dialog;
 }
 LinearLayout optionCard(String title,String subtitle,String symbol,Runnable action){
  LinearLayout item=panel();item.setPadding(dp(16),dp(14),dp(16),dp(14));item.setOrientation(0);item.setGravity(Gravity.CENTER_VERTICAL);
  item.setBackground(new android.graphics.drawable.RippleDrawable(android.content.res.ColorStateList.valueOf(0x20298d6b),rounded(darkMode?0xff233d32:0xfff1f6f2,18),null));
  TextView icon=label(symbol,22,darkMode?0xff67d6ad:0xff206e53,"sans-serif-medium");icon.setGravity(Gravity.CENTER);LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(dp(36),dp(40));ip.setMarginEnd(dp(10));item.addView(icon,ip);
  LinearLayout copy=new LinearLayout(this);copy.setOrientation(1);copy.addView(label(title,16,ink,"sans-serif-medium"));space(copy,4);copy.addView(label(subtitle,12,muted,"sans-serif"));item.addView(copy,new LinearLayout.LayoutParams(0,-2,1));
  TextView arrow=label("›",23,muted,"sans-serif");arrow.setPadding(dp(8),0,0,0);item.addView(arrow);
  item.setFocusable(true);item.setOnClickListener(v->action.run());return item;
 }
 void openMenu(View anchor){
  LinearLayout content=form();final AlertDialog[] dialog={null};
  String[] names={"Perfil y meta","Recordatorios","Importar CSV","Exportar CSV","Historial","Acerca de"};
  String[] descriptions={"Tus datos y tu peso deseado","Días y hora de pesaje","Recuperá tus mediciones","Guardá una copia de tus registros","Consultá y editá tus mediciones","GuiTracker · Versión y licencia"};
  String[] icons={"○","◷","↓","↑","≡","i"};
  Runnable[] actions={this::profile,this::reminders,()->csvOptions(true),()->csvOptions(false),()->{viewingHistory=true;show();},this::about};
  for(int i=0;i<names.length;i++){final int index=i;content.addView(optionCard(names[i],descriptions[i],icons[i],()->{dialog[0].dismiss();actions[index].run();}));space(content,10);}
  dialog[0]=sheet("Tu espacio",content);
 }
 void csvOptions(boolean importing){
  LinearLayout content=form();LinearLayout card=panel();card.setBackground(rounded(darkMode?0xff233d32:0xfff1f6f2,20));
  card.addView(label(importing?"Recuperá tu progreso":"Tu progreso, a salvo",21,ink,"sans-serif-medium"));space(card,12);
  TextView description=label(importing?"Elegí un CSV de GuiTracker. Antes de importar, te pediremos confirmar. Las fechas coincidentes se reemplazan; las demás se conservan.":"Guardá tus mediciones en un archivo CSV. Podés conservarlo fuera del teléfono y volver a importarlo cuando lo necesites.",14,muted,"sans-serif");description.setLineSpacing(dp(4),1);card.addView(description);space(card,14);card.addView(label("El CSV incluye fecha y peso. El perfil y los recordatorios se configuran por separado.",12,muted,"sans-serif"));content.addView(card);
  AlertDialog d=new AlertDialog.Builder(this).setTitle(importing?"Importar CSV":"Exportar CSV").setView(content).setPositiveButton("Elegir archivo",(a,b)->{if(importing)importCsv();else exportCsv();}).setNegativeButton("Cancelar",null).create();d.show();decorateDialog(d);
 }
 void about(){
  LinearLayout content=form();LinearLayout identity=panel();identity.setBackground(rounded(darkMode?0xff233d32:0xfff1f6f2,22));
  identity.addView(label("GuiTracker",28,ink,"sans-serif-medium"));space(identity,8);
  String version="";long code=0;try{android.content.pm.PackageInfo info=getPackageManager().getPackageInfo(getPackageName(),0);version=info.versionName;code=Build.VERSION.SDK_INT>=28?info.getLongVersionCode():info.versionCode;}catch(Exception ignored){}
  identity.addView(label("Versión "+version+" · Código "+code,14,muted,"sans-serif"));space(identity,16);identity.addView(label("Tu progreso, día a día.",16,ink,"sans-serif"));space(identity,6);identity.addView(label("Funciona sin internet. Tus datos quedan en tu teléfono.",13,muted,"sans-serif"));content.addView(identity);space(content,12);
  LinearLayout license=panel();license.setBackground(rounded(darkMode?0xff233d32:0xfff1f6f2,22));license.addView(label("Código libre · GPL v3",18,ink,"sans-serif-medium"));space(license,10);
  TextView terms=label("Software libre bajo GNU General Public License versión 3 (GPL-3.0-only). Podés usar, estudiar, modificar y redistribuir el código según los términos de esta licencia. Se distribuye sin garantía.",13,muted,"sans-serif");terms.setLineSpacing(dp(4),1);license.addView(terms);space(license,12);license.addView(button("Leer licencia GPL v3",this::license));content.addView(license);space(content,10);content.addView(label("El código fuente correspondiente se entrega junto con la APK en GuiTracker-proyecto.zip.",12,muted,"sans-serif"));sheet("Acerca de",content);
 }
 void license(){
  try(InputStream in=getAssets().open("LICENSE.txt")){ByteArrayOutputStream bytes=new ByteArrayOutputStream();byte[] buffer=new byte[4096];int n;while((n=in.read(buffer))!=-1)bytes.write(buffer,0,n);LinearLayout content=form();TextView full=label(bytes.toString("UTF-8"),12,ink,"monospace");full.setTextIsSelectable(true);content.addView(full);sheet("GNU GPL · Versión 3",content);}catch(Exception e){error("No se pudo abrir la licencia");}
 }
 void showHistory(){
  ArrayList<String> ds=dates();Collections.reverse(ds);
  if(ds.isEmpty()){LinearLayout empty=panel();empty.addView(label("Tu historia empieza acá",21,ink,"sans-serif-medium"));space(empty,10);empty.addView(label("Todavía no hay mediciones registradas.",14,muted,"sans-serif"));addPanel(empty);return;}
  body.addView(label("Tocá una medición para editarla o eliminarla.",13,muted,"sans-serif"));space(body,16);
  for(String day:ds){body.addView(optionCard(num(records().optDouble(day))+" kg",date(LocalDate.parse(day)),"·",()->measurement(LocalDate.parse(day))));space(body,10);}
 }
 @Override public void onBackPressed(){if(viewingHistory){viewingHistory=false;show();}else super.onBackPressed();}
 EditText input(LinearLayout parent,String name,String value,boolean decimal){
  space(parent,12);parent.addView(label(name,12,muted,"sans-serif-medium"));space(parent,7);
  EditText field=new EditText(this);field.setSingleLine(true);field.setText(value);field.setTextColor(ink);field.setTextSize(17);field.setPadding(dp(14),dp(14),dp(14),dp(14));field.setBackground(rounded(darkMode?0xff233d32:0xfff1f6f2,14));if(decimal)field.setInputType(8194);parent.addView(field,new LinearLayout.LayoutParams(-1,-2));space(parent,4);return field;
 }
 LinearLayout form(){LinearLayout layout=new LinearLayout(this);layout.setOrientation(1);layout.setPadding(dp(20),dp(14),dp(20),dp(16));layout.setBackgroundColor(card);return layout;}
 double value(EditText e,double min,double max){double v=Double.parseDouble(e.getText().toString().trim().replace(',','.'));if(!Double.isFinite(v)||v<min||v>max||Math.abs(v*10-Math.rint(v*10))>0.00001)throw new IllegalArgumentException();return v;}
 void error(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
 void pick(LocalDate d,java.util.function.Consumer<LocalDate> cb){DatePickerDialog p=new DatePickerDialog(this,(v,y,m,day)->cb.accept(LocalDate.of(y,m+1,day)),d.getYear(),d.getMonthValue()-1,d.getDayOfMonth());p.getDatePicker().setMaxDate(System.currentTimeMillis());p.show();}
 void measurement(LocalDate initial){LinearLayout l=form();final LocalDate[] selected={initial};Button day=button(date(initial),()->{});day.setOnClickListener(v->pick(selected[0],d->{selected[0]=d;day.setText(date(d));}));l.addView(day);EditText w=input(l,"Peso (kg · un decimal)",records().has(initial.toString())?num(records().optDouble(initial.toString())):"",true);l.addView(text("Si ya hay un registro en la fecha elegida, se reemplazará.",13,muted));AlertDialog dialog=new AlertDialog.Builder(this).setTitle("Registrar peso").setView(l).setPositiveButton("Guardar",null).setNegativeButton("Cancelar",null).setNeutralButton(records().has(initial.toString())?"Eliminar":null,(d,i)->new AlertDialog.Builder(this).setMessage("¿Eliminar el registro del "+date(initial)+"?").setPositiveButton("Eliminar",(a,b)->{records().remove(initial.toString());save();show();}).setNegativeButton("Cancelar",null).show()).create();dialog.setOnShowListener(v->dialog.getButton(-1).setOnClickListener(a->{try{double weight=value(w,20,500);records().put(selected[0].toString(),weight);if(!initial.equals(selected[0]))records().remove(initial.toString());save();dialog.dismiss();show();}catch(Exception e){error("Ingresá un peso entre 20 y 500 kg, con hasta un decimal.");}}));dialog.show();decorateDialog(dialog);}
 void profile(){LinearLayout l=form();EditText name=input(l,"Nombre",data.optString("name"),false);final LocalDate[] birth={LocalDate.parse(data.optString("birth","1990-01-01"))};Button dob=button("Nacimiento: "+date(birth[0]),()->{});dob.setOnClickListener(v->pick(birth[0],d->{birth[0]=d;dob.setText("Nacimiento: "+date(d));}));l.addView(dob);EditText height=input(l,"Altura (cm)",data.has("height")?num(data.optDouble("height")):"",true);EditText goal=input(l,"Peso meta (kg · opcional)",data.has("goal")?num(data.optDouble("goal")):"",true);l.addView(text("Tema automático según el teléfono. Perfil para adultos de 18 años o más.",13,muted));ScrollView s=new ScrollView(this);s.addView(l);AlertDialog d=new AlertDialog.Builder(this).setTitle("Tu perfil").setView(s).setPositiveButton("Guardar",null).setNegativeButton("Cancelar",null).create();d.setOnShowListener(v->d.getButton(-1).setOnClickListener(a->{try{String n=name.getText().toString().trim();if(n.isEmpty()||Period.between(birth[0],LocalDate.now()).getYears()<18)throw new Exception();double h=value(height,100,250);double g=goal.getText().toString().trim().isEmpty()?0:value(goal,20,500);put("name",n);put("birth",birth[0].toString());put("height",h);if(g>0)put("goal",g);else data.remove("goal");save();d.dismiss();show();}catch(Exception e){error("Revisá nombre, fecha (18 años o más), altura y meta. Usá hasta un decimal.");}}));d.show();decorateDialog(d);}
 void reminders(){LinearLayout l=form();String[] days={"Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo"};CheckBox[] boxes=new CheckBox[7];int mask=data.optInt("days");for(int i=0;i<7;i++){boxes[i]=new CheckBox(this);boxes[i].setText(days[i]);boxes[i].setTextColor(ink);boxes[i].setButtonTintList(android.content.res.ColorStateList.valueOf(darkMode?0xff67d6ad:0xff206e53));boxes[i].setChecked((mask&(1<<i))!=0);l.addView(boxes[i]);}final int[] time={data.optInt("hour",9),data.optInt("minute",0)};Button t=button(String.format(Locale.ROOT,"Hora: %02d:%02d",time[0],time[1]),()->{});t.setOnClickListener(v->new TimePickerDialog(this,(view,h,m)->{time[0]=h;time[1]=m;t.setText(String.format(Locale.ROOT,"Hora: %02d:%02d",h,m));},time[0],time[1],true).show());l.addView(t);l.addView(text("Desmarcá todos los días para desactivar. Android puede demorar el aviso para ahorrar batería.",13,muted));ScrollView reminderScroll=new ScrollView(this);reminderScroll.addView(l);AlertDialog reminderDialog=new AlertDialog.Builder(this).setTitle("Recordatorio de pesaje").setView(reminderScroll).setPositiveButton("Guardar",(d,w)->{int selected=0;for(int i=0;i<7;i++)if(boxes[i].isChecked())selected|=1<<i;put("days",selected);put("hour",time[0]);put("minute",time[1]);save();if(selected!=0&&Build.VERSION.SDK_INT>=33&&checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=0)requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},7);schedule(this);error("Recordatorios guardados");}).setNegativeButton("Cancelar",null).create();reminderDialog.show();decorateDialog(reminderDialog);}
 static void schedule(Context c){JSONObject d=read(c);AlarmManager am=(AlarmManager)c.getSystemService(ALARM_SERVICE);PendingIntent p=PendingIntent.getBroadcast(c,0,new Intent(c,ReminderReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);am.cancel(p);int mask=d.optInt("days");if(mask==0)return;ZonedDateTime at=Domain.nextReminder(ZonedDateTime.now(),mask,d.optInt("hour",9),d.optInt("minute",0));am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at.toInstant().toEpochMilli(),p);}
 public static class ReminderReceiver extends BroadcastReceiver {public void onReceive(Context c,Intent i){NotificationManager n=(NotificationManager)c.getSystemService(NOTIFICATION_SERVICE);n.createNotificationChannel(new NotificationChannel("weight","Recordatorios de pesaje",NotificationManager.IMPORTANCE_DEFAULT));PendingIntent open=PendingIntent.getActivity(c,0,new Intent(c,MainActivity.class),PendingIntent.FLAG_IMMUTABLE);try{n.notify(1,new Notification.Builder(c,"weight").setSmallIcon(android.R.drawable.ic_menu_today).setContentTitle("GuiTracker · Hora de pesarte").setContentText("Registrá tu peso y seguí tu evolución.").setContentIntent(open).setAutoCancel(true).build());}catch(SecurityException ignored){}schedule(c);}}
 public static class BootReceiver extends BroadcastReceiver {public void onReceive(Context c,Intent i){schedule(c);}}
 void exportCsv(){Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.setType("text/csv");i.addCategory(Intent.CATEGORY_OPENABLE);i.putExtra(Intent.EXTRA_TITLE,"GuiTracker-"+LocalDate.now()+".csv");startActivityForResult(i,10);}
 void importCsv(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("*/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,11);}
 @Override public void onActivityResult(int req,int result,Intent intent){super.onActivityResult(req,result,intent);if(result!=RESULT_OK||intent==null)return;Uri uri=intent.getData();try{if(req==10){try(Writer w=new OutputStreamWriter(getContentResolver().openOutputStream(uri),"UTF-8")){w.write("fecha,peso_kg\r\n");for(String d:dates())w.write(d+","+String.format(Locale.ROOT,"%.1f",records().optDouble(d))+"\r\n");}error("CSV exportado");}else if(req==11){JSONObject incoming=new JSONObject();try(BufferedReader r=new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri),"UTF-8"))){for(Map.Entry<String,Double> entry:Domain.parseCsv(r,LocalDate.now()).entrySet())incoming.put(entry.getKey(),entry.getValue());}new AlertDialog.Builder(this).setTitle("Importar "+incoming.length()+" registros").setMessage("Se conservarán los otros registros. Las fechas coincidentes se reemplazarán. El CSV contiene fecha y peso; el perfil se configura por separado.").setPositiveButton("Importar",(d,w)->{try{Iterator<String> keys=incoming.keys();while(keys.hasNext()){String k=keys.next();records().put(k,incoming.getDouble(k));}save();show();error("Importación completada");}catch(Exception e){error("No se pudo guardar la importación");}}).setNegativeButton("Cancelar",null).show();}}catch(Exception e){error("No se pudo procesar el archivo. Formato: fecha,peso_kg; ejemplo: 2026-10-23,75.4. No se importó ningún registro.");}}
 class Chart extends View {
  final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
  Chart(){super(MainActivity.this);setContentDescription("Gráfico anual de peso. Valores detallados disponibles en Historial, dentro del menú.");setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
  @Override protected void onDraw(Canvas canvas){
   super.onDraw(canvas);float density=getResources().getDisplayMetrics().density;canvas.save();canvas.scale(density,density);
   float w=getWidth()/density,h=getHeight()/density,left=34,right=w-12,top=20,bottom=h-30;
   if(right<=left||bottom<=top){canvas.restore();return;}
   LocalDate start=end.minusDays(364);ArrayList<String> visible=new ArrayList<>();double low=Double.MAX_VALUE,high=-Double.MAX_VALUE;
   for(String day:dates()){LocalDate d=LocalDate.parse(day);if(!d.isBefore(start)&&!d.isAfter(end)){visible.add(day);double value=records().optDouble(day);low=Math.min(low,value);high=Math.max(high,value);}}
   int lineColor=darkMode?0xff67d6ad:0xff248467;double goal=data.optDouble("goal",0);
   if(visible.isEmpty()){p.setTypeface(Typeface.create("sans-serif",0));p.setTextAlign(Paint.Align.CENTER);p.setTextSize(13);p.setColor(muted);canvas.drawText("Todavía no hay mediciones",w/2,h/2-6,p);p.setTextSize(11);canvas.drawText("en este período",w/2,h/2+14,p);canvas.restore();return;}
   if(goal>0){low=Math.min(low,goal);high=Math.max(high,goal);}double padding=Math.max(1,(high-low)*0.15);low=Math.floor(low-padding);high=Math.ceil(high+padding);
   p.setTypeface(Typeface.create("sans-serif",0));p.setTextSize(10);
   for(int i=0;i<4;i++){float y=top+(bottom-top)*i/3;p.setColor(darkMode?0xff2a4036:0xffedf1ed);p.setStrokeWidth(1);canvas.drawLine(left,y,right,y,p);p.setTextAlign(Paint.Align.LEFT);p.setColor(muted);canvas.drawText(String.format(Locale.ROOT,"%.0f",high-(high-low)*i/3),0,y+3,p);}
   if(goal>0){float y=bottom-(float)((goal-low)/(high-low))*(bottom-top);p.setColor(darkMode?0xffe4be76:0xffbd9553);p.setStrokeWidth(1.3f);p.setPathEffect(new DashPathEffect(new float[]{4,5},0));canvas.drawLine(left,y,right,y,p);p.setPathEffect(null);}
   Path path=new Path();float firstX=0,lastX=0,lastY=0;
   for(int i=0;i<visible.size();i++){String day=visible.get(i);float x=left+(float)java.time.temporal.ChronoUnit.DAYS.between(start,LocalDate.parse(day))/364*(right-left);float y=bottom-(float)((records().optDouble(day)-low)/(high-low))*(bottom-top);if(i==0){firstX=x;path.moveTo(x,y);}else path.lineTo(x,y);lastX=x;lastY=y;}
   if(visible.size()>1){Path fill=new Path(path);fill.lineTo(lastX,bottom);fill.lineTo(firstX,bottom);fill.close();p.setShader(new LinearGradient(0,top,0,bottom,darkMode?0x3867d6ad:0x30248467,0x00248467,Shader.TileMode.CLAMP));p.setStyle(Paint.Style.FILL);canvas.drawPath(fill,p);p.setShader(null);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2.6f);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeJoin(Paint.Join.ROUND);p.setColor(lineColor);canvas.drawPath(path,p);p.setStyle(Paint.Style.FILL);}
   if(visible.size()<=40)for(String day:visible){float x=left+(float)java.time.temporal.ChronoUnit.DAYS.between(start,LocalDate.parse(day))/364*(right-left);float y=bottom-(float)((records().optDouble(day)-low)/(high-low))*(bottom-top);p.setColor(lineColor);canvas.drawCircle(x,y,2.5f,p);}
   p.setColor(card);canvas.drawCircle(lastX,lastY,5.5f,p);p.setColor(lineColor);canvas.drawCircle(lastX,lastY,3.8f,p);
   p.setColor(muted);p.setTextSize(10);for(int i=0;i<=3;i++){LocalDate d=start.plusDays(364*i/3);String month=d.format(DateTimeFormatter.ofPattern("MMM yy",new Locale("es","AR")));p.setTextAlign(i==0?Paint.Align.LEFT:i==3?Paint.Align.RIGHT:Paint.Align.CENTER);canvas.drawText(month,left+(right-left)*i/3,bottom+23,p);}canvas.restore();
  }
 }
}
