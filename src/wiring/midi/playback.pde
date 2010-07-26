#define MAX_MELODY_LENGTH 8
#define PLAYBACK_PIN 7

int currFreq = -1;
unsigned long lastMillis;

void playback(boolean noteOn, int index, int note, unsigned long delta) {
  unsigned long deltaMillis = (delta * getMicrosecondsPerQuarterNote()) / (((long) getTimeDivision()) * 1000);
  int freq = getFreq(note);
  
  if(currFreq != -1) {
    unsigned long currMillis = millis();
    if(currMillis < lastMillis + deltaMillis)
      delay(lastMillis - currMillis + deltaMillis);
  }
  
  if(noteOn) {
    tone(PLAYBACK_PIN, freq);
    logi("Playing note: ", note);
  }
  else if(freq == currFreq) {
    noTone(PLAYBACK_PIN);
    logi("Stopping note: ", note);
  }
  else {
    logs("Ignoring event.");
  }
  
  lastMillis = millis();
  currFreq = freq;
}

void endPlayback() {
  noTone(PLAYBACK_PIN);
}
