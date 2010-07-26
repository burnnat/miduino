#include "msg.h"

byte readSerialByte() {
  if(Serial.available() > 0) {
    return readSerialByteWithoutRequest();
  }
  else {
    Serial.write(MSG_SEND_MORE);
    
    while(Serial.available() <= 0) {}
  
  
    return readSerialByteWithoutRequest();
  }
}

byte readSerialByteWithoutRequest() {
    // necessary for proper serial reading
    delay(1);
    
    lastByte = Serial.read();
    count++;
    
    return lastByte;
}
