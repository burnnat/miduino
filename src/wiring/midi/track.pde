
void processTrack(long size) {
  long counter = 0;
  
  while(counter < size) {
    counter += processEvent();
  }
  
  logi("Track counter", counter);
}
