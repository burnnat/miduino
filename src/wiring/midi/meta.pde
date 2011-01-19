// The number of microseconds per quarter note (i.e. the current tempo)
long microseconds = 500000;

/*
 * Handles a tempo event with the given values.
 */
void processTempoEvent(int paramIndex, byte param) {
  byte bits = 16 - 8*paramIndex;
  microseconds = (microseconds & ~((long) 0xFF << bits)) | ((long) param << bits);
}

long getMicrosecondsPerQuarterNote() {
  return microseconds;
}
