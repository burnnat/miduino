#define PIN_OUT 2

void setup() {
  Serial.begin(9600);
  pinMode(PIN_OUT, OUTPUT);
}

void loop() {
  digitalWrite(PIN_OUT, HIGH);
  Serial.println("Note On");
  delay(1000);
  digitalWrite(PIN_OUT, LOW);
  Serial.println("Note Off");
  delay(1000);
}

