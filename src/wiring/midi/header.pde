int format;
int trackCount;
int timeDivision;

/*
 * Parses useful information out of the MIDI file header.
 */
void processHeader(long size) {
  // size should always be 6
  // do we want to bother checking?
  
  format = readInt();
  trackCount = readInt();
  timeDivision = readInt();
  
  logs("Processed header info.");
}

int getFileFormat() {
  return format;
}

int getTrackCount() {
  return trackCount;
}

int getTimeDivision() {
  return timeDivision;
}
