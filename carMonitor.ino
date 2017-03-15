#include <U8glib.h>
#include <TinyGPS.h>
#define XSIZE 45
#define XDISP 18
//U8GLIB_ST7920_128X64 u8g(39, 37, 35, U8G_PIN_NONE); 
#define VOLT 8
#define WOTBTN 20
#define IDLEBTN 21
#define O2 12
#define AFM 9
#define ATS 11
#define CTS 10
#define COIL 19
#define BANK2 2
#define BANK1 3
#define MAX_POINTS_OPENING_RECORDS 50

#define VOLTFACTOR 26.5

//TinyGPS gps;

int vals[XSIZE];
int vals2[XSIZE];
int vals3[XSIZE];
int pos;
int subiendo;
int subiendo2;
int subiendo3;
int value;
int value2;
int value3;
int value4;
int value5;
int randNumber;
int randNumber3;
int randNumber2;
int randNumber4;
int randNumber5;
int vIdle;
int vWOT;
int lastTimeSerial = 0;
int lastTimePulseins = 0;
int lastTimeGetVal = 0;
int difGetval, difPulseins;
unsigned long lastTimeRPMCheck;
unsigned int bangsSinceLastTime;
volatile unsigned int numBangs, numBank1, numBank2, rpm, bangsForProcessing, pointsOpeningArrayPosition;
volatile unsigned long lastChange;
volatile unsigned int timePointsOpen[MAX_POINTS_OPENING_RECORDS];
float bank1amount, bank2amount;
float flat, flon;

void gpsdump(TinyGPS &gps);
void printFloat(double f, int digits = 2);

void pointsOpeningRPM(){
    numBangs = numBangs + 1;
}

void pointsOpeningBANK1(){
//  if(pointsOpeningArrayPosition < MAX_POINTS_OPENING_RECORDS){
    lastChange = micros();
//  }
}

void pointsClosingBANK1(){
//  if(pointsOpeningArrayPosition < MAX_POINTS_OPENING_RECORDS){
    long currentTime = micros();
//    if(currentTime > lastChange){
//      timePointsOpen[pointsOpeningArrayPosition] = micros() - lastChange;
//      pointsOpeningArrayPosition++;
//    }
//  }
      timePointsOpen[0] = micros() - lastChange;
}

int searchMaxVals(){
  int response = 0;
  for(int i = 0; i < XSIZE; i++){
    if(vals[i] > response)response = vals[i];
  }
  return response;
}
int searchMaxVals2(){
  int response = 0;
  for(int i = 0; i < XSIZE; i++){
    if(vals2[i] > response)response = vals2[i];
  }
  return response;
}
int searchMaxVals3(){
  int response = 0;
  for(int i = 0; i < XSIZE; i++){
    if(vals3[i] > response)response = vals3[i];
  }
  return response;
}
int searchMinVals(){
  int response = 1024;
  for(int i = 0; i < XSIZE; i++){
    if(vals[i] < response)response = vals[i];
  }
  return response;
}
int searchMinVals2(){
  int response = 1024;
  for(int i = 0; i < XSIZE; i++){
    if(vals2[i] < response)response = vals2[i];
  }
  return response;
}
int searchMinVals3(){
  int response = 1024;
  for(int i = 0; i < XSIZE; i++){
    if(vals3[i] < response)response = vals3[i];
  }
  return response;
}

//void draw(void) {
//  u8g.drawLine(17,18,17,63);
//  u8g.drawLine(17,18,63,18);
//  u8g.drawLine(17,63,63,63);
//  u8g.drawLine(63,18,63,63);
//  if(vIdle != 0 && vWOT == 0)u8g.drawRFrame(9, 7, 35, 9, 2);
//  else u8g.drawRBox(9, 7, 35, 9, 2);
//  if(vWOT != 0 && vIdle == 0)u8g.drawRFrame(46, 7, 35, 9, 2);
//  else u8g.drawRBox(46, 7, 35, 9, 2);
//    if((vWOT == 0 && vIdle == 0) || (vWOT == 1 && vIdle == 1))u8g.drawRFrame(83, 7, 35, 9, 2);
//  else u8g.drawRBox(83, 7, 35, 9, 2);
//  int maxGraph = searchMaxVals() + 1;
//  int minGraph = searchMinVals() - 1;
//  int maxGraph2 = searchMaxVals2() + 1;
//  int minGraph2 = searchMinVals2() - 1;
//  int maxGraph3 = searchMaxVals3() + 1;
//  int minGraph3 = searchMinVals3() - 1;
//  int dif1 = maxGraph - minGraph;
//  int dif2 = maxGraph2 - minGraph2;
//  int dif3 = maxGraph3 - minGraph3;
//  for(int i = 0; i < XSIZE; i++){
//    u8g.drawPixel(i+XDISP, 30-10*((vals[(i+pos)%XSIZE])-minGraph)/(dif1));
//    u8g.drawPixel(i+XDISP, 45-10*((vals2[(i+pos)%XSIZE])-minGraph2)/(dif2));
//    u8g.drawPixel(i+XDISP, 60-10*((vals3[(i+pos)%XSIZE])-minGraph3)/(dif3));
//  }
//  
//}
//
//
//void drawText(void) {
//  // graphic commands to redraw the complete screen should be placed here
//
//  u8g.setFont(u8g_font_04b_03b);
//  //u8g.setPrintPos(0, 26);
//    u8g.setPrintPos(3, 5);
//    u8g.print("Motronic 1.0 Adaptive Monitor");
//    
//    u8g.setPrintPos(19, 14);
//    u8g.print("Idle");
//    
//    u8g.setPrintPos(50, 14);
//    u8g.print("Partial");
//    
//    u8g.setPrintPos(93, 14);
//    u8g.print("WOT");
//    
//    
//    
//  u8g.drawStr(0, 26,"AFM" /*+ vals[pos]*/);
//    u8g.setPrintPos(0, 33);
//    u8g.print((value/1024.0f)*5.0f);
//  //u8g.setPrintPos(0, 41);
//  u8g.drawStr(0, 41,"CTS" /*+ vals[pos]*/);
//    u8g.setPrintPos(0, 48);
//    u8g.print((value2/1024.0f)*5.0f);
//  //
//  u8g.drawStr(0, 56,"AIT" /*+ vals[pos]*/);
//  u8g.setPrintPos(0, 63);
//    u8g.print((value3/1024.0f)*5.0f);
//    
//  u8g.drawStr(65, 26,"RPM" /*+ vals[pos]*/);
//    u8g.setPrintPos(65, 33);
//    u8g.print(rpm);
//  //u8g.setPrintPos(0, 41);
//  u8g.drawStr(65, 41,"BNK1" /*+ vals[pos]*/);
//    u8g.setPrintPos(65, 48);
//    u8g.print(bank1amount);
//  //
//  u8g.drawStr(65, 56,"BNK2" /*+ vals[pos]*/);
//  u8g.setPrintPos(65, 63);
//    u8g.print(bank2amount);
//    
//  u8g.drawStr(96, 26,"KPH" /*+ vals[pos]*/);
//    u8g.setPrintPos(96, 33);
//    u8g.print(gps.f_speed_kmph());
//  //u8g.setPrintPos(0, 41);
//  u8g.drawStr(96, 41,"O2" /*+ vals[pos]*/);
//    u8g.setPrintPos(96, 48);
//    u8g.print((value4/1024.0f)*25.0f);
//  //
//  u8g.drawStr(96, 56,"VOLT" /*+ vals[pos]*/);
//  u8g.setPrintPos(96, 63);
//    u8g.print((value5/1024.0f));
//  
//  
//}

void setup(void) {
  // flip screen, if required
  //u8g.setRot90();
  Serial.begin(115200);
  Serial2.begin(9600);
  pos = 0;
  pinMode(34, OUTPUT);
  pinMode(40, OUTPUT);
  pinMode(46, OUTPUT);
  pinMode(52, OUTPUT);
  pinMode(13, OUTPUT);
  pinMode(WOTBTN, INPUT);
  pinMode(IDLEBTN, INPUT);
  pinMode(COIL, INPUT);
  //digitalWrite(COIL, HIGH);
  pinMode(BANK2, INPUT);
  //digitalWrite(BANK2, HIGH);
  pinMode(BANK1, INPUT);
  //digitalWrite(BANK1, HIGH);
  value = 500;
  value2 = 600;
  value3 = 700;
  value5 = 700;
  vWOT = 0;
  vIdle = 0;
  randNumber = 0;
  randNumber2 = 0;
  randNumber3 = 0;
  randNumber4 = 0;
  randNumber5 = 0;
  for(int i = 0; i < XSIZE; i++){
    vals[i]=0;
    vals2[i]=0;
    vals3[i]=0;
  }
  numBangs = 0;
  lastTimeRPMCheck = 0;
  bangsSinceLastTime = 0;
  randomSeed(analogRead(0));
  //attachInterrupt(digitalPinToInterrupt(COIL), pointsOpeningRPM, RISING);
}

void diagLeds(){
  if(value4 > 128){
    digitalWrite(34, HIGH);
  } else {
    digitalWrite(34, LOW);
  }
  if(vIdle == 0 && vWOT == 0){
    digitalWrite(40, HIGH);
    digitalWrite(46, LOW);
  } else if (vIdle == 1){
    digitalWrite(46, HIGH);
    digitalWrite(40, LOW);
  } else {
    digitalWrite(40, LOW);
    digitalWrite(46, LOW);
  }

  if(value > 256){
    digitalWrite(52, HIGH);
  } else {
    digitalWrite(52, LOW);
  }
  
}

void getVal(){ //real
  if(digitalRead(WOTBTN) == LOW) vWOT = 1;
  else vWOT = 0;
  if(digitalRead(IDLEBTN) == LOW) vIdle = 1;
  else vIdle = 0; 
  //A8 AFM INPUT value
  //A7 CTS INPUT value2
  //A5 AIT INPUT value3
  value = analogRead(AFM);
  value2 = analogRead(CTS);
  value3 = analogRead(ATS);
  value4 = analogRead(O2);
  value5 = analogRead(VOLT)*VOLTFACTOR;
  vals[pos] = value;
  vals2[pos] = value2;
  vals3[pos] = value3;
  pos = (pos + 1)%XSIZE;

  diagLeds();
}

/*void getVal(){  //ficticio
  randNumber = random(3);
  randNumber2 = random(3);
  randNumber3 = random(3);
  randNumber4 = random(2);
  randNumber5 = random(2);
  if(randNumber5 == 1) vWOT = 1;
  else vWOT = 0;
  if(randNumber4 == 1) vIdle = 1;
  else vIdle = 0; 
  //A8 AFM INPUT value
  //A7 CTS INPUT value2
  //A5 AIT INPUT value3
  if(randNumber == 1) //sube
  value = value + 1;
  else value = value - 1;
  if(randNumber3 == 1) //sube
  value3 = value3 + 1;
  else value3 = value3 - 1;
  if(randNumber2 == 1) //sube
  value2 = value2 + 1;
  else value2 = value2 - 1;
  if(value > 1023) value = value - 15;
  if(value2 > 1023) value2 = value2 - 15;
  if(value3 > 1023) value3 = value3 - 15;
  if(value < 0) value = value + 15;
  if(value2 < 0) value2 = value2 + 15;
  if(value3 < 0) value3 = value3 + 15;
  vals[pos] = value;
  vals2[pos] = value2;
  vals3[pos] = value3;
  pos = (pos + 1)%XSIZE;
  value5 = analogRead(VOLT)*VOLTFACTOR;
}*/

void turnOffLed(){
  digitalWrite(13, LOW);    // turn the LED off by making the voltage LOW
}
  
void turnOnLed(){
  digitalWrite(13, HIGH);   // turn the LED on (HIGH is the voltage level)
}

void loop(void) {
  // picture loop
  
  // Number of coil pulses
//  numBangs = 0;
//  attachInterrupt(digitalPinToInterrupt(COIL), pointsOpeningRPM, RISING);
//  delay(250);
//  detachInterrupt(digitalPinToInterrupt(COIL));
  //  numBangs = ((60000/1000)*numBangs)/2;
//  rpm=numBangs * 80;

//  int max_points_opening_records = rpm / 200;
  long currentTime = millis();
  int max_points_opening_records = 2;
  // ahora promedio el tiempo de apertura de los inyectores
  // primero obtengo varios valores
  float promedioRPMBajo = 0;
  float promedioAperturaInyectores = 0;
  float promedioAperturaInyectores2 = 0;
  int counterinj1 = 0;
  int counterinj2 = 0;
  int counterrpm = 0;
  for(int i = 0; i < max_points_opening_records; i++){
    int valrpm = pulseIn(COIL, LOW);
    int valinj1 = pulseIn(BANK1, HIGH);
    int valinj2 = pulseIn(BANK2, HIGH);
    if(valrpm > 100){
      counterrpm++;
      promedioRPMBajo = promedioRPMBajo + valrpm;
    }
    if(valinj1 > 1000){
      counterinj1++;
      promedioAperturaInyectores = promedioAperturaInyectores + valinj1;
    }
    if(valinj2 > 1000){
      counterinj2++;
      promedioAperturaInyectores2 = promedioAperturaInyectores2 + valinj2;
    }
  }
   
  if(counterrpm == 0){
    counterrpm = 1;
  }
  if(counterinj1 == 0){
    counterinj1 = 1;
  }
  if(counterinj2 == 0){
    counterinj2 = 1;
  }
  promedioRPMBajo = promedioRPMBajo/counterrpm;
  promedioAperturaInyectores = promedioAperturaInyectores/counterinj1;
  promedioAperturaInyectores2 = promedioAperturaInyectores2/counterinj2;
  bank1amount=promedioAperturaInyectores/1000;
  bank2amount=promedioAperturaInyectores2/1000;
  if(promedioRPMBajo > 2400){
    if(promedioRPMBajo > 3500){
      if(promedioRPMBajo > 4500){
        if(promedioRPMBajo > 5500){
          if(promedioRPMBajo > 6500){
            if(promedioRPMBajo > 7500){
              if(promedioRPMBajo > 8500){
                if(promedioRPMBajo > 10500){
                  if(promedioRPMBajo > 11500){
                    if(promedioRPMBajo > 12500){
                      if(promedioRPMBajo > 13500){
                        if(promedioRPMBajo > 14500){
                          if(promedioRPMBajo > 15500){ // factor = 1.3
                            rpm=20800000/promedioRPMBajo;
                          } else { // factor = 1.292929293
                            rpm=20686868/promedioRPMBajo;
                          } 
                        } else { // factor = 1.289099526
                          rpm=20625592/promedioRPMBajo;
                        } 
                      } else { // factor = 1.2774336283
                        rpm=20389380/promedioRPMBajo;
                      } 
                    } else { // factor = 1.251028807
                      rpm=20016460/promedioRPMBajo;
                    } 
                  } else { // factor = 1.217656012
                    rpm=19482496/promedioRPMBajo;
                  } 
                } else { // factor = 1.230769231
                  rpm=19692307/promedioRPMBajo;
                } 
              } else { // factor = 1.27388535
                rpm=20382165/promedioRPMBajo;
              } 
            } else { // factor = 1.18852459
              rpm=19016393/promedioRPMBajo;
            } 
          } else { // factor = 1.22467357
            rpm=19594777/promedioRPMBajo;
          } 
        } else { // factor = 1.173402868
          rpm=18774445/promedioRPMBajo;
        } 
      } else { // factor = 1.139841689
        rpm=18237467/promedioRPMBajo;
      }
    } else { // factor = 1.09721662
      rpm=17555465/promedioRPMBajo;
    }
  } else { // factor = 1.027359071
    rpm=16437745/promedioRPMBajo;
  }
  
  

  turnOnLed();
  getVal();
  turnOffLed();
  

//  u8g.firstPage();
//  do {
//    draw();
//    drawText();
//  } while( u8g.nextPage() );
      
      //delay(200);
  // rebuild the picture after some delay
  
  bool newdata = false;
  unsigned long start = millis();

  // Every 5 seconds we print an update
//  while (millis() - start < 100) {
//    if (Serial2.available()) {
//      char c = Serial2.read();
//      //Serial.print(c);  // uncomment to see raw GPS data
//      if (gps.encode(c)) {
//        newdata = true;
//        // break;  // uncomment to print new data immediately!
//      }
//    }
//  }
  
//  if (newdata) {
//    Serial.println("Acquired Data");
//    Serial.println("-------------");
//    gpsdump(gps);
//    Serial.println("-------------");
//    Serial.println();
//  }

  imprimirValoresSerial();

  long nowTime = millis();
  if(nowTime - currentTime < 200000){
    delay(200 - (nowTime - currentTime));
  }
}



void imprimirValoresSerial(){
  int currentTime = millis();
  int dif = currentTime - lastTimeSerial;
  lastTimeSerial = currentTime;
  // este método se encarga de enviar todos los datos recopilados por el arduino mediante el puerto serie.
  // el primer caracter es un # para indicar que se comienza a enviar el string con el mensaje a decodificar
  String message = String("#");
  // se concatenan los siguientes campos, separados por ";"
  // #RPM;BNK1;BNK2;KPH;HGHT;LAT;LONG;AFM;CTS;AIT;VOLT;IDLE1/WOT2/PARTIAL3
  message = message + rpm;
  message = message + String(";");
  message = message + bank1amount;
  message = message + String(";");
  message = message + bank2amount;
  message = message + String(";");
//  message = message + gps.f_speed_kmph();
  message = message + flat;
  message = message + String(";");
//  message = message + gps.altitude()/1000.0f;
  message = message + flat;
  message = message + String(";");
  message = message + flat;
  message = message + String(";");
  message = message + flon;
  message = message + String(";");
  message = message + (value/1024.0f)*5.0f;
  message = message + String(";");
  message = message + (value2/1024.0f)*5.0f;
  message = message + String(";");
  message = message + (value3/1024.0f)*5.0f;
  message = message + String(";");
  message = message + (value4/1024.0f)*25.0f;
  message = message + String(";");
  message = message + (value5/1024.0f);
  message = message + String(";");
  if(vIdle != 0 && vWOT == 0)
    message = message + String("1");
  else if(vWOT != 0 && vIdle == 0)
    message = message + String("2");
  else if((vWOT == 0 && vIdle == 0) || (vWOT == 1 && vIdle == 1))
    message = message + String("3");
  else 
    message = message + String("UNKNOWN");
  
//  message = message + String(";") + String(dif);
//  message = message + String(";") + String(difGetval);
//  message = message + String(";") + String(difPulseins);
  Serial.println(message);
  difGetval = millis() - currentTime;
  
}

//void gpsdump(TinyGPS &gps)
//{
//  long lat, lon;
//  
//  unsigned long age, date, time, chars;
//  int year;
//  byte month, day, hour, minute, second, hundredths;
//  unsigned short sentences, failed;
//
//  gps.get_position(&lat, &lon, &age);
//  Serial.print("Lat/Long(10^-5 deg): "); Serial.print(lat); Serial.print(", "); Serial.print(lon); 
//  Serial.print(" Fix age: "); Serial.print(age); Serial.println("ms.");
//  
//  // On Arduino, GPS characters may be lost during lengthy Serial.print()
//  // On Teensy, Serial prints to USB, which has large output buffering and
//  //   runs very fast, so it's not necessary to worry about missing 4800
//  //   baud GPS characters.
//
//  gps.f_get_position(&flat, &flon, &age);
//  Serial.print("Lat/Long(float): "); printFloat(flat, 5); Serial.print(", "); printFloat(flon, 5);
//  Serial.print(" Fix age: "); Serial.print(age); Serial.println("ms.");
//
//  gps.get_datetime(&date, &time, &age);
//  Serial.print("Date(ddmmyy): "); Serial.print(date); Serial.print(" Time(hhmmsscc): ");
//    Serial.print(time);
//  Serial.print(" Fix age: "); Serial.print(age); Serial.println("ms.");
//
//  gps.crack_datetime(&year, &month, &day, &hour, &minute, &second, &hundredths, &age);
//  Serial.print("Date: "); Serial.print(static_cast<int>(month)); Serial.print("/"); 
//    Serial.print(static_cast<int>(day)); Serial.print("/"); Serial.print(year);
//  Serial.print("  Time: "); Serial.print(static_cast<int>(hour)); Serial.print(":"); 
//    Serial.print(static_cast<int>(minute)); Serial.print(":"); Serial.print(static_cast<int>(second));
//    Serial.print("."); Serial.print(static_cast<int>(hundredths));
//  Serial.print("  Fix age: ");  Serial.print(age); Serial.println("ms.");
//
//  Serial.print("Alt(cm): "); Serial.print(gps.altitude()); Serial.print(" Course(10^-2 deg): ");
//    Serial.print(gps.course()); Serial.print(" Speed(10^-2 knots): "); Serial.println(gps.speed());
//  Serial.print("Alt(float): "); printFloat(gps.f_altitude()); Serial.print(" Course(float): ");
//    printFloat(gps.f_course()); Serial.println();
//  Serial.print("Speed(knots): "); printFloat(gps.f_speed_knots()); Serial.print(" (mph): ");
//    printFloat(gps.f_speed_mph());
//  Serial.print(" (mps): "); printFloat(gps.f_speed_mps()); Serial.print(" (kmph): ");
//    printFloat(gps.f_speed_kmph()); Serial.println();
//
//  gps.stats(&chars, &sentences, &failed);
//  Serial.print("Stats: characters: "); Serial.print(chars); Serial.print(" sentences: ");
//    Serial.print(sentences); Serial.print(" failed checksum: "); Serial.println(failed);
//}
//
//void printFloat(double number, int digits)
//{
//  // Handle negative numbers
//  if (number < 0.0) {
//     Serial.print('-');
//     number = -number;
//  }
//
//  // Round correctly so that print(1.999, 2) prints as "2.00"
//  double rounding = 0.5;
//  for (uint8_t i=0; i<digits; ++i)
//    rounding /= 10.0;
//  
//  number += rounding;
//
//  // Extract the integer part of the number and print it
//  unsigned long int_part = (unsigned long)number;
//  double remainder = number - (double)int_part;
//  Serial.print(int_part);
//
//  // Print the decimal point, but only if there are digits beyond
//  if (digits > 0)
//    Serial.print("."); 
//
//  // Extract digits from the remainder one at a time
//  while (digits-- > 0) {
//    remainder *= 10.0;
//    int toPrint = int(remainder);
//    Serial.print(toPrint);
//    remainder -= toPrint;
//  }
//}
