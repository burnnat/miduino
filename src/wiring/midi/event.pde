/*
 * Define constant values for MIDI events, as given in the MIDI spec
 */
#define DELTA_TIME_VALUE_MASK 0x7F

#define DELTA_TIME_END_MASK 0x80
#define DELTA_TIME_END_VALUE 0x80

#define EVENT_TYPE_MASK 0xF0
#define EVENT_CHANNEL_MASK 0x0F

#define NOTE_OFF_EVENT_TYPE 0x80
#define NOTE_ON_EVENT_TYPE 0x90
#define KEY_AFTERTOUCH_EVENT_TYPE 0xA0
#define CONTROL_CHANGE_EVENT_TYPE 0xB0
#define PROGRAM_CHANGE_EVENT_TYPE 0xC0
#define CHANNEL_AFTERTOUCH_EVENT_TYPE 0xD0
#define PITCH_WHEEL_CHANGE_EVENT_TYPE 0xE0

#define META_EVENT_TYPE 0xFF
#define SYSTEM_EVENT_TYPE 0xF0

#define META_SEQ_COMMAND 0x00
#define META_TEXT_COMMAND 0x01
#define META_COPYRIGHT_COMMAND 0x02
#define META_TRACK_NAME_COMMAND 0x03
#define META_INST_NAME_COMMAND 0x04
#define META_LYRICS_COMMAND 0x05
#define META_MARKER_COMMAND 0x06
#define META_CUE_POINT_COMMAND 0x07
#define META_CHANNEL_PREFIX_COMMAND 0x20
#define META_END_OF_TRACK_COMMAND 0x2F
#define META_SET_TEMPO_COMMAND 0x51
#define META_SMPTE_OFFSET_COMMAND 0x54
#define META_TIME_SIG_COMMAND 0x58
#define META_KEY_SIG_COMMAND 0x59
#define META_SEQ_SPECIFIC_COMMAND 0x7F

unsigned long deltaTime;
int eventType;
int eventChannel;
int parameter1;
int parameter2;

/*
 * Reads an event type code from the currently open file, and handles it accordingly.
 */
int processEvent() {
  logDivision(false);
  
  int counter = 0;
  deltaTime = 0;
  
  int b;
  
  do {
    b = readByte();
    counter++;
    
    deltaTime = (deltaTime << 7) | (b & DELTA_TIME_VALUE_MASK);
  } while((b & DELTA_TIME_END_MASK) == DELTA_TIME_END_VALUE);
  
  logi("Delta", deltaTime);
  
  b = readByte();
    counter++;

  boolean runningMode = true;
  // New events will always have a most significant bit of 1
  // Otherwise, we operate in 'running mode', whereby the last
  // event command is assumed and only the parameters follow
  if(b >= 128) {
    eventType = (b & EVENT_TYPE_MASK) >> 4;
    eventChannel = b & EVENT_CHANNEL_MASK;
    runningMode = false;
  }
  
  logi("Event type", eventType);
  logi("Event channel", eventChannel);
  
  // handle meta-events and track events separately
  if(eventType == (META_EVENT_TYPE & EVENT_TYPE_MASK) >> 4
     && eventChannel == (META_EVENT_TYPE & EVENT_CHANNEL_MASK)) {
    counter += processMetaEvent();
  }
  else {
    counter += processTrackEvent(runningMode, b);
  }
  
  return counter;
}

/*
 * Reads a meta-event command and size from the file, performing the appropriate action
 * for the command.
 *
 * NB: currently, only tempo changes are handled - all else is useless for our organ.
 */
int processMetaEvent() {
  int command = readByte();
  int size = readByte();
  
  logi("Meta event length", size);
  
  for(int i = 0; i < size; i++) {
    byte data = readByte();
    
    switch(command) {
      case META_SET_TEMPO_COMMAND:
        processTempoEvent(i, data);
    }
  }
  
  return size + 2;
}

/*
 * Reads a track event from the file, either as a full event or in running mode (to
 * be determined automatically), and takes appropriate playback action.
 */
int processTrackEvent(boolean runningMode, int lastByte) {
  int count = 0;
  
  if(runningMode) {
    parameter1 = getLastByte();
  }
  else {
    parameter1 = readByte(); 
    count++;
  }
  
  logi("Parameter 1", parameter1);
  
  int eventShift = eventType << 4;
  
  switch(eventShift) {
    case NOTE_OFF_EVENT_TYPE:
    case NOTE_ON_EVENT_TYPE:
    case KEY_AFTERTOUCH_EVENT_TYPE:
    case CONTROL_CHANGE_EVENT_TYPE:
    case PITCH_WHEEL_CHANGE_EVENT_TYPE:
    default:
      parameter2 = readByte();
      count++;

      logi("Parameter 2", parameter2);
      
      break;
    case PROGRAM_CHANGE_EVENT_TYPE:
    case CHANNEL_AFTERTOUCH_EVENT_TYPE:
      break;
  }
  
  if(eventShift == NOTE_ON_EVENT_TYPE) {
    processNoteOnEvent(deltaTime, parameter1, parameter2);
  }
  else if(eventShift == NOTE_OFF_EVENT_TYPE) {
    processNoteOffEvent(deltaTime, parameter1, parameter2);
  }
  else {
    addDelta(deltaTime);
  }
  
  return count;
}
