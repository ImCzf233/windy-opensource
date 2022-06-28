package space.emptiness.events.rendering;


import space.emptiness.eventapi.events.Event;

public class EventDrawText implements Event {
   public String text;

   public EventDrawText(String text) {
      this.text = text;
   }
}
