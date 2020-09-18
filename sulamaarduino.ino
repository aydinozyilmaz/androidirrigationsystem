
#include <LiquidCrystal.h> /* LCD kullanýmý için kütüphane dahil edilmelidir. */
#include <SoftwareSerial.h>/*Bluetooth kullanimi için kütüphane dahil edilmelidir.*/
#include <AFMotor.h>/*Motor sürücü için kütüphane dahil edilmelidir.*/
SoftwareSerial Genotronex(0, 1); /* RX, TX.*/
int data;/*Gerekli Tanýmlamalar*/
int in1=9;
int in2=8;
int buzzer=6;
char BluetoothData; 
const int toprak_deger=A0;
int olcum_sonucu;
int kontrol = 0;
LiquidCrystal lcd(12, 11, 5, 4, 3, 2); /* LCD'nin baðlandýðý Arduino pinleri. */
void setup()
{
 lcd.begin(16, 2); /* Kullandýðýmýz LCD'nin sütun ve satýr sayýsýný belirtmeliyiz.*/
  
 pinMode(6,OUTPUT);/*Gerekli pin çýkýþlarý*/
 pinMode(in1,OUTPUT);
 pinMode(in2,OUTPUT);  
 Genotronex.begin(9600);/*Seri iletiþimi baþlatýr.*/
 Serial.begin(9600);
}
void loop()
{
  if (Genotronex.available())/*Bluetooth nesne kontrolü.*/
  {
   BluetoothData=Genotronex.read();/*Bluetooth deðerini okuyoruz.*/
    
    if (BluetoothData=='1') /*Sula butonuna basýlýrsa.*/
{
      digitalWrite(in1,HIGH);/*Motorun dönme iþlemi.*/
      digitalWrite(in2,LOW);
      lcd.clear();/*LCD temizleme.*/
      lcd.print("Sulama yapiliyor...");
      digitalWrite(buzzer,HIGH);/*Buzzer çalýþmasý.*/
      kontrol = 1;/*Nem aralýðý için komtrol deðeri.*/
     }
    else if(BluetoothData=='0')/*Sulamayý durdur butonuna basýlýrsa*/
    {
      digitalWrite(in1,LOW);/*Motoru durdurma iþlemi*/
      digitalWrite(in2,LOW);
      lcd.clear();/*LCD temizleme.*/
      digitalWrite(buzzer,LOW);/*Buzzer durdurulmasý.*/
      lcd.print("Sulama tamamlandi...");
      kontrol = 0;/*Nem aralýðý için komtrol deðeri.*/
    }
  }

  lcd.setCursor(0, 1); /*Ýmlecin yeri 1. satýr 0. sütun olarak ayarlandý.*/ 
  olcum_sonucu=analogRead(toprak_deger);/*Analog pininden deðer okunmasý.*/
  lcd.print("Nem = ");
  lcd.print(olcum_sonucu);/*Sensörden okunan deðer sonucunun yazdýrýlmasý.*/
  if(olcum_sonucu<=500 && kontrol ==1)/*Sulama için gerekli kontrol.*/
  {
    lcd.clear();/*LCD temizleme.*/
    lcd.print("Sulama yapmayiniz...");
  }
  else if(kontrol == 0 && olcum_sonucu >= 700 )/*Sulama için gerekli kontrol.*/
  {
    lcd.clear();/*LCD temizleme.*/
    lcd.print("Sulama yapiniz..."); }}	