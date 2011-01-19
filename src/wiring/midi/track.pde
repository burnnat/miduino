/*
 * Loops through a track of the MIDI file, processing the data as it goes.
 */

void processTrack(long size) {
  long counter = 0;
  
  while(counter < size) {
    counter += processEvent();
  }
  
  logi("Track counter", counter);
}
