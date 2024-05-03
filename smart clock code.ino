#include <WiFi.h>
#include <Firebase_ESP_Client.h>
//======================================== 

//======================================== Insert your network credentials.
#define WIFI_SSID "SLT-4G_C9311"
#define WIFI_PASSWORD "sanananda"
//======================================== 

//Provide the token generation process info.
#include "addons/TokenHelper.h"

//Provide the RTDB payload printing info and other helper functions.
#include "addons/RTDBHelper.h"

// Defines the Digital Pin of the "On Board LED".
#define On_Board_LED 2

// Insert Firebase project API Key
#define API_KEY "AIzaSyCvQRpKu3hI7vB5oBVdHIPpRcbcty0bqVo"

// Insert RTDB URLefine the RTDB URL */
#define DATABASE_URL "https://final-fa3e2-default-rtdb.firebaseio.com/" 

#include <NTPClient.h>
#include <WiFiUdp.h>
#include "time.h"

#include <TFT_eSPI.h> // Graphics and font library for ST7735 driver chip
#include <SPI.h>

TFT_eSPI tft = TFT_eSPI();  // Invoke library, pins defined in User_Setup.h

//Define Firebase Data object.
FirebaseData fbdo;

// Define firebase authentication.
FirebaseAuth auth;

// Definee firebase configuration.
FirebaseConfig config;

//======================================== Millis variable to read data from firebase database.
unsigned long sendDataPrevMillis = 0;
const long intervalMillis = 60000; //--> Read data from firebase database every 60 seconds.
const long interval_alarm = 10000;
unsigned long previousMillis = 0;
int totalAlarms = 0;
bool moreAlarms = true;

bool moreTasks = true;
int totalTasks = 0;

const int ON_BOARD_LED_PIN = 21; // GPIO 21 (D2) on ESP32

// Variables to manage LED activation time and duration
bool isLEDEnabled = false;
unsigned long ledActivationTime = 0;
const unsigned long ledDuration = 30000; // LED duration in milliseconds (e.g., 60 seconds)


//======================================== 

// Boolean variable for sign in status.
bool signupOK = false;

String read_date;
String read_time;

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");

const long utcOffsetInSeconds = 5 * 3600 + 30 * 60; // Replace YOUR_TIMEZONE_OFFSET with the offset of your time zone in hours


//____________________________ VOID SETUP
void setup() {

  tft.init();
  tft.setRotation(0);
  tft.fillScreen(TFT_BLACK);

  tft.setTextColor(TFT_YELLOW, TFT_BLACK); // Note: the new fonts do not draw the background colour

  // put your setup code here, to run once:
  
  Serial.begin(115200);
  Serial.println();

  pinMode(On_Board_LED, OUTPUT);
  pinMode(ON_BOARD_LED_PIN, OUTPUT);

  //---------------------------------------- The process of connecting the WiFi on the ESP32 to the WiFi Router/Hotspot.
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.println("---------------Connection");
  Serial.print("Connecting to : ");
  Serial.println(WIFI_SSID);
  while (WiFi.status() != WL_CONNECTED){
    Serial.print(".");

    digitalWrite(On_Board_LED, HIGH);
    delay(250);
    digitalWrite(On_Board_LED, LOW);
    delay(250);
  }
  digitalWrite(On_Board_LED, LOW);
  Serial.println();
  Serial.print("Successfully connected to : ");
  Serial.println(WIFI_SSID);
  //Serial.print("IP : ");
  //Serial.println(WiFi.localIP());
  Serial.println("---------------");
  //---------------------------------------- 

  // Assign the api key (required).
  config.api_key = API_KEY;

  // Assign the RTDB URL (required).
  config.database_url = DATABASE_URL;

  // Sign up.
  Serial.println();
  Serial.println("---------------Sign up");
  Serial.print("Sign up new user... ");
  if (Firebase.signUp(&config, &auth, "", "")){
    Serial.println("ok");
    signupOK = true;
  }
  else{
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }
  Serial.println("---------------");
  
  // Assign the callback function for the long running token generation task.
  config.token_status_callback = tokenStatusCallback; //--> see addons/TokenHelper.h
  
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  // Initialize and synchronize time using NTP
  configTime(0, 0, "pool.ntp.org", "time.nist.gov");
  timeClient.begin();
  
}


//____________________________

//____________________________ VOID LOOP
void loop() {
  // put your main code here, to run repeatedly:
  
  if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > intervalMillis || sendDataPrevMillis == 0)){
    sendDataPrevMillis = millis();

    //---------------------------------------- The process of reading data from firebase database.
    Serial.println();
    Serial.println("---------------Get Data");
    digitalWrite(On_Board_LED, HIGH);


    timeClient.update();
    struct tm timeinfo;

    if (!getLocalTime(&timeinfo)) {
      
      
      Serial.println("Failed to obtain time");
      return;
    }
    // Adjust time based on the time zone offset
    time_t adjustedTime = mktime(&timeinfo) + utcOffsetInSeconds;
    localtime_r(&adjustedTime, &timeinfo);

    // Format time as a string
    char timeString[50];
    char dateString[50];
    strftime(timeString, sizeof(timeString), "%H:%M", &timeinfo);
    strftime(dateString, sizeof(dateString), "%Y-%m-%d", &timeinfo);

    // Display time on the Serial Monitor
    Serial.print("Current time in your timezone: ");
    Serial.println(timeString);
    //tft.setTextColor(0xFBE0);
    tft.drawRightString(timeString,180,80,7);
    //tft.setTextColor(0xffff);
    tft.drawRightString(dateString,185,130,4);

    while (moreAlarms) {
        String alarmPath = "/Users/kanim/Alarms/alarm" + String(totalAlarms + 1); // Construct path for next alarm node

        

        if (Firebase.RTDB.getString(&fbdo, alarmPath + "/time")) {
            // If the alarm node exists, increment the totalAlarms count
            
            totalAlarms++;
        } else {
            // If the alarm node doesn't exist, there are no more alarms
            moreAlarms = false;
        }
    }
    
    while (moreTasks) {
        String taskPath = "/Users/kanim/Reminders/Reminder" + String(totalTasks + 1); // Construct path for next alarm node

        if (Firebase.RTDB.getString(&fbdo, taskPath + "/date")) {
            // If the alarm node exists, increment the totalAlarms count
            totalTasks++;
        } else {
            // If the alarm node doesn't exist, there are no more alarms
            moreTasks = false;
        }
    }
    
    for (int i = 1; i <= totalAlarms; ++i) {

      String alarmPath = "/Users/kanim/Alarms/alarm" + String(i);

      if (Firebase.RTDB.getBool(&fbdo, alarmPath + "/isActive")) {
        bool isActive = fbdo.boolData();

        if (Firebase.RTDB.getString(&fbdo, alarmPath + "/time")) {
          String readTime = fbdo.stringData();

          

          if (isActive && readTime == timeString) {
            Serial.println("Date and time match for Alarm" + String(i));
            //tft.drawRightString("Alarm Time",185,150,3);
          

            isLEDEnabled = true;
            ledActivationTime = millis();
          }
        }
      }

        else {
          Serial.println(fbdo.errorReason());
    }
    }
    
    for (int i = 1; i <= totalTasks; ++i){

      String taskPath = "/Users/kanim/Reminders/Reminder" + String(i);

      if (Firebase.RTDB.getString(&fbdo, taskPath + "/date")) {
        String taskDate = fbdo.stringData();

        

        if (Firebase.RTDB.getString(&fbdo, taskPath + "/reminderText")) {
          String taskText = fbdo.stringData();

          

          if (taskDate == dateString) {

            //Serial.println("Time of task" + String(i) + " is "+taskTime);
            Serial.println("Your reminder is "+taskText);
            tft.drawRightString(taskText,140,150,4);
            
          }

        
            
          
          }
        }
        delay(5000);
    } 
          
  
  
     
          
    Serial.println();

    digitalWrite(On_Board_LED, LOW);
    Serial.println("---------------");
    //----------------------------------------  
  }

  if (isLEDEnabled) {
    unsigned long currentTime = millis();
    if (currentTime - ledActivationTime < ledDuration) {
      digitalWrite(ON_BOARD_LED_PIN, HIGH);
    }
    else {
      digitalWrite(ON_BOARD_LED_PIN, LOW);
      isLEDEnabled = false;
    }
    }
}

//%H:%M:%S