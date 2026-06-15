package example.actors

import example.models.Subject
import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.persistence.typed.PersistenceId
import org.apache.pekko.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}

object SubjectPersistentActor {

  sealed trait Command

  case class AddSubject(subject: Subject, replyTo: ActorRef[String]) extends Command
  case class GetSubjects(replyTo: ActorRef[Seq[Subject]]) extends Command

  sealed trait Event

  case class SubjectAdded(subject: Subject) extends Event

  case class SubjectState(subjects: Seq[Subject])

  val emptyState: SubjectState = SubjectState(Seq.empty)

  def apply(): Behavior[Command] = {

    EventSourcedBehavior[Command, Event, SubjectState](

      persistenceId = PersistenceId.ofUniqueId("subject-actor"),

      emptyState = emptyState,

      commandHandler = { (state, command) =>

        command match {

          case AddSubject(subject, replyTo) =>

            Effect
              .persist(SubjectAdded(subject))
              .thenRun { _ =>
                replyTo ! "Subject Added"
              }

          case GetSubjects(replyTo) =>

            replyTo ! state.subjects

            Effect.none
        }
      },

      eventHandler = { (state, event) =>

        event match {

          case SubjectAdded(subject) =>

            state.copy(
              subjects = state.subjects :+ subject
            )
        }
      }
    )
  }
}