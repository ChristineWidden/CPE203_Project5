import java.util.*;

/*
EventScheduler: ideally our way of controlling what happens in our virtual world
 */

final class EventScheduler
{
   private final PriorityQueue<Event> eventQueue;
   private final Map<Entity, List<Event>> pendingEvents;
   private final double timeScale;

   public EventScheduler(double timeScale)
   {
      this.eventQueue = new PriorityQueue<>(new EventComparator());
      this.pendingEvents = new HashMap<>();
      this.timeScale = timeScale;
   }


   /**
    * EventScheduler
    * Creates an event and schedules when it will occur
    * @param entity the entity associated with the action
    * @param action the action to take
    * @param afterPeriod number of milliseconds until the event
    */
   public void scheduleEvent(Entity entity, Action action, long afterPeriod)
   {
      long time = System.currentTimeMillis() +
              (long)(afterPeriod * timeScale);
      Event event = new Event(action, time, entity);

      eventQueue.add(event);

      // update list of pending events for the given entity
      List<Event> pending = pendingEvents.getOrDefault(entity,
              new LinkedList<>());
      pending.add(event);
      pendingEvents.put(entity, pending);
   }

   /**
    * EventScheduler
    * Removes the events for the given entity from the lists of upcoming events
    * @param entity
    */
   public void unscheduleAllEvents(Entity entity)
   {
      List<Event> pending = pendingEvents.remove(entity);

      if (pending != null)
      {
         for (Event event : pending)
         {
            eventQueue.remove(event);
         }
      }
   }

   /**
    * EventScheduler
    * Removes an event from the list of pending events if the event exists
    * @param event
    */
   private void removePendingEvent(Event event)
   {
      List<Event> pending = pendingEvents.get(event.getEntity());

      if (pending != null)
      {
         pending.remove(event);
      }
   }

   /**
    * EventScheduler
    * Grabs the next event from the event queue,
    * removes it from the list of pending events, and executes it
    * @param time
    */
   public void updateOnTime(long time)
   {
      while (!eventQueue.isEmpty() &&
              eventQueue.peek().getTime() < time)
      {
         Event next = eventQueue.poll();

         removePendingEvent(next);

         next.getAction().executeAction(this);
      }
   }


}
