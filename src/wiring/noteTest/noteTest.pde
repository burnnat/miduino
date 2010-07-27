
void setup(){
  pinMode(11, OUTPUT);
  Serial.begin(9600);
}

void loop(){
  digitalWrite(11, HIGH);
  Serial.println("Note On");
  delay(1000);
  digitalWrite(11, LOW);
  Serial.println("Note Off");
  delay(1000);
}

