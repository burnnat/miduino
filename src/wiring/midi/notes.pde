int index = 0;

unsigned long accDelta = 0;

void addDelta(unsigned long delta) {
  accDelta = accDelta + delta;
}

void resetDelta() {
  accDelta = 0;
}

void processNoteOnEvent(unsigned long delta, int note, int velocity) {
  addDelta(delta);
  
  playback(true, index, note, accDelta);
  index++;
  
  resetDelta();
}

void processNoteOffEvent(unsigned long delta, int note, int velocity) {
  addDelta(delta);
  
  playback(false, index, note, accDelta);
  index++;
  
  resetDelta();
}
