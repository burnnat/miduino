// The number of microseconds per quarter note
long microseconds = 500000;

void processTempoEvent(int paramIndex, byte param) {
  byte bits = 16 - 8*paramIndex;
  microseconds = (microseconds & ~((long) 0xFF << bits)) | ((long) param << bits);
}

long getMicrosecondsPerQuarterNote() {
  return microseconds;
}
