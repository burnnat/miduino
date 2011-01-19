/*
 * Main file for MIDI library - handles communication with client
 * and reading data from source MIDI file (whether a preset or streamed
 * over the serial port).
 */

#include "msg.h"

#define HEADER_CHUNK_ID 0x4D546864  // MThd
#define TRACK_CHUNK_ID 0x4D54726B   // MTrk

#define READ_PRESET 0
#define READ_SERIAL 1

#define logging false

byte readMode;
byte lastByte;

long count = 0;

void setup() {
  Serial.begin(9600);
  preparePlayback();
}

void loop() {
  if (Serial.available() > 0) {
    byte cmd = readSerialByteWithoutRequest();
    
    switch(cmd) {
      case MSG_PLAY_PRESET:
        setPreset(readSerialByteWithoutRequest());
        readMode = READ_PRESET;
        break;
      case MSG_PLAY_STREAM:
        readMode = READ_SERIAL;
        break;
      case MSG_CHECK_ALIVE:
        Serial.write(MSG_CONFIRM_ALIVE);
        Serial.write(getPresetCount());
        return;
      default:
        return;
    }
    
    processChunk(); // header chunk
    
    if(getFileFormat() == 0) {
      for(int i = 0; i < getTrackCount(); i++) {
        processChunk();
      }
      clearPlayback();
      Serial.write(MSG_END_MIDI);
    }
    else {
      logs("MIDI file not format 0.");
      Serial.write(MSG_ERR_MIDI_FORMAT);
    }
    
//    Serial.flush();
  }
}

/*void setLogging(boolean shouldLog) {
  logging = shouldLog;
}*/

void logDivision(boolean major) {
  if(!logging)
    return;
  
  if(major) {
    Serial.println("===========================");    
  }
  else {
    Serial.println("----------------------");
  }
}

void logs(char* string) {
  if(!logging)
    return;
  
  Serial.println(string);
}

void logi(char* label, int data) {
  if(!logging)
    return;
  
  Serial.print(label);
  Serial.print(": ");
  Serial.println(data);
}

void logl(char* label, long data) {
  if(!logging)
    return;
  
  Serial.print(label);
  Serial.print(": ");
  Serial.println(data);
}

boolean charArrayEqual(char* array1, char* array2, int length) {
  for(int i = 0; i < length; i++) {
    if(array1[i] != array2[i])
      return false;
  }
  
  return true;
}

byte getLastByte() {
  return lastByte;
}

byte readByte() {
  switch(readMode) {
    case READ_SERIAL:
      lastByte = readSerialByte();
      break;
    case READ_PRESET:
    default:
      lastByte = readPresetByte();
      break;
  }
  
  return lastByte;
}

int readInt() {
  return readByte() << 8 | readByte();
}

long readLong() {
  return (long) readByte() << 24 | (long) readByte() << 16 | readByte() << 8 | readByte();
}

void processChunk() {
  boolean valid = true;
  
  long chunkID = readLong();
  long size = readLong();
  
  logDivision(true);
  logi("Chunk ID", chunkID);
  logi("Chunk Size", size);
  
  if(chunkID == HEADER_CHUNK_ID) {
    processHeader(size);
    
    logi("File format", getFileFormat());
    logi("Track count", getTrackCount());
    logi("Time division", getTimeDivision());
  }
  else if(chunkID == TRACK_CHUNK_ID) {
    processTrack(size);
  }
}
