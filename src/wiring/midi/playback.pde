#define PLAYBACK_SPEAKER 11
#define PLAYBACK_G4 2
#define PLAYBACK_A4 3
#define PLAYBACK_B4 4
#define PLAYBACK_C5 5
#define PLAYBACK_D5 6
#define PLAYBACK_E5 7
#define PLAYBACK_F5 8
#define PLAYBACK_FS5 9
#define PLAYBACK_G5 10

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
  
  doTone(note, freq, noteOn);
  
  lastMillis = millis();
}

void doTone(int note, int freq, boolean start) {
  switch(note) {
    case NOTE_G4:
      organNote(PLAYBACK_G4, start);
      break;
    case NOTE_A4:
      organNote(PLAYBACK_A4, start);
      break;
    case NOTE_B4:
      organNote(PLAYBACK_B4, start);
      break;
    case NOTE_C5:
      organNote(PLAYBACK_C5, start);
      break;
    case NOTE_D5:
      organNote(PLAYBACK_D5, start);
      break;
    case NOTE_E5:
      organNote(PLAYBACK_E5, start);
      break;
    case NOTE_F5:
      organNote(PLAYBACK_F5, start);
      break;
    case NOTE_FS5:
      organNote(PLAYBACK_FS5, start);
      break;
    case NOTE_G5:
      organNote(PLAYBACK_G5, start);
      break;
    default:
      if(start) {
        tone(PLAYBACK_SPEAKER, freq);
        currFreq = freq;
      }
      else if(freq == currFreq) {
        noTone(PLAYBACK_SPEAKER);
      }
  }
}

void organNote(int pin, boolean start) {
  digitalWrite(pin, start ? HIGH : LOW);
}

void endPlayback() {
  organNote(PLAYBACK_G4, false);
  organNote(PLAYBACK_A4, false);
  organNote(PLAYBACK_B4, false);
  organNote(PLAYBACK_C5, false);
  organNote(PLAYBACK_D5, false);
  organNote(PLAYBACK_E5, false);
  organNote(PLAYBACK_F5, false);
  organNote(PLAYBACK_FS5, false);
  organNote(PLAYBACK_G5, false);
  noTone(PLAYBACK_SPEAKER);
}
