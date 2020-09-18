
#include <LiquidCrystal.h> /* LCD kullan�m� i�in k�t�phane dahil edilmelidir. */
#include <SoftwareSerial.h>/*Bluetooth kullanimi i�in k�t�phane dahil edilmelidir.*/
#include <AFMotor.h>/*Motor s�r�c� i�in k�t�phane dahil edilmelidir.*/
SoftwareSerial Genotronex(0, 1); /* RX, TX.*/
int data;/*Gerekli Tan�mlamalar*/
int in1=9;
int in2=8;
int buzzer=6;
char BluetoothData; 
const int toprak_deger=A0;
int olcum_sonucu;
int kontrol = 0;
LiquidCrystal lcd(12, 11, 5, 4, 3, 2); /* LCD'nin ba�land��� Arduino pinleri. */
void setup()
{
 lcd.begin(16, 2); /* Kulland���m�z LCD'nin s�tun ve sat�r say�s�n� belirtmeliyiz.*/
  
 pinMode(6,OUTPUT);/*Gerekli pin ��k��lar�*/
 pinMode(in1,OUTPUT);
 pinMode(in2,OUTPUT);  
 Genotronex.begin(9600);/*Seri ileti�imi ba�lat�r.*/
 Serial.begin(9600);
}
void loop()
{
  if (Genotronex.available())/*Bluetooth nesne kontrol�.*/
  {
   BluetoothData=Genotronex.read();/*Bluetooth de�erini okuyoruz.*/
    
    if (BluetoothData=='1') /*Sula butonuna bas�l�rsa.*/
{
      digitalWrite(in1,HIGH);/*Motorun d�nme i�lemi.*/
      digitalWrite(in2,LOW);
      lcd.clear();/*LCD temizleme.*/
      lcd.print("Sulama yapiliyor...");
      digitalWrite(buzzer,HIGH);/*Buzzer �al��mas�.*/
      kontrol = 1;/*Nem aral��� i�in komtrol de�eri.*/
     }
    else if(BluetoothData=='0')/*Sulamay� durdur butonuna bas�l�rsa*/
    {
      digitalWrite(in1,LOW);/*Motoru durdurma i�lemi*/
      digitalWrite(in2,LOW);
      lcd.clear();/*LCD temizleme.*/
      digitalWrite(buzzer,LOW);/*Buzzer durdurulmas�.*/
      lcd.print("Sulama tamamlandi...");
      kontrol = 0;/*Nem aral��� i�in komtrol de�eri.*/
    }
  }

  lcd.setCursor(0, 1); /*�mlecin yeri 1. sat�r 0. s�tun olarak ayarland�.*/ 
  olcum_sonucu=analogRead(toprak_deger);/*Analog pininden de�er okunmas�.*/
  lcd.print("Nem = ");
  lcd.print(olcum_sonucu);/*Sens�rden okunan de�er sonucunun yazd�r�lmas�.*/
  if(olcum_sonucu<=500 && kontrol ==1)/*Sulama i�in gerekli kontrol.*/
  {
    lcd.clear();/*LCD temizleme.*/
    lcd.print("Sulama yapmayiniz...");
  }
  else if(kontrol == 0 && olcum_sonucu >= 700 )/*Sulama i�in gerekli kontrol.*/
  {
    lcd.clear();/*LCD temizleme.*/
    lcd.print("Sulama yapiniz..."); }}	