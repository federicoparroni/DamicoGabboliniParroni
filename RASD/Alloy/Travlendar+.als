module Travlendar

open util/integer as Integer
open util/boolean as boolean
open util/ordering[DateTime] as DTO
open util/ordering[Time] as TO

sig DateTime{}

sig Time{}

sig Schedule{
	wakeUpTime: one Time,
	appointmentList: some Appointment,
	scheduledAppointmentList: some ScheduledAppointment,
	startingLocation: one Coordinates
}

sig Path{
	lenght: one Int,
	source: one ScheduledAppointment,
	dest: one ScheduledAppointment
}
{lenght >= 0}

abstract sig Constraint{
	travelMean: one TravelMean,
	maxTravelDistance: one Int
}
{maxTravelDistance >= 0}

sig ConstraintOnAppointment extends Constraint{
}

sig ConstraintOnSchedule extends Constraint{
	weather: some Weather,
	timeSlot: one TimeSlot
}

sig Appointment{
	schedule: one Schedule,
	startingTime: one DateTime,
	timeSlot: lone TimeSlot,
	location: one Coordinates,
	numberInvolvedPeople: one Int,
	constraints: some Constraint
}
{numberInvolvedPeople >= 0}

sig ScheduledAppointment{
	schedule: one Schedule,
	appointment: one Appointment,
	startingTravelTime: one DateTime,
	ETA: one DateTime,
	endingTime: one DateTime, 
	location: one Coordinates
}
{startingTravelTime in DTO/prevs[endingTime]
ETA in DTO/nexts[startingTravelTime] and ETA in DTO/prevs[endingTime] }

// if two scheduled appointments have are relative to the same appointment then they must belong to different schedules
fact AppointmentOnMultipleSchedules {
	all s1,s2 : ScheduledAppointment | s1 != s2 and s1.appointment = s2.appointment implies s1.schedule != s2.schedule
}

pred NoOverlappingScheduledAppointment {
	all s1,s2 : ScheduledAppointment | s1 != s2 implies  s1.endingTime != s2.startingTravelTime and (s1.endingTime in DTO/prevs[s2.startingTravelTime] or s2.endingTime in DTO/prevs[s1.startingTravelTime])
} 

sig TimeSlot{
	start: one Time,
	end: one Time
}

sig Coordinates{
	longitude: one Int,
	latitude: one Int
}
{longitude >= 0 latitude >= 0}



// FACT 1 no Time unlinked
// FACT 2 no Coordinates unlinked
// FACT 3 no boolean unlinked



sig Weather{}

//sig OptimizingCriteria{}

abstract sig TravelMean{}
abstract sig PublicTravelMean extends TravelMean{}
abstract sig SharedTravelMean extends TravelMean{}
abstract sig PrivateTravelMean extends TravelMean{}

sig Train extends PublicTravelMean{}
sig Bus extends PublicTravelMean{}
sig Tram extends PublicTravelMean{}
sig Taxi extends PublicTravelMean{}
sig Underground extends PublicTravelMean{}

sig CarSharing extends SharedTravelMean{}
sig BikeSharing extends SharedTravelMean{}

sig Bike extends PrivateTravelMean{}
sig Car extends PrivateTravelMean{}
sig Walking extends PrivateTravelMean{}




sig Gps{}

sig User{}

sig SchedulePlan{}


pred show(){}


run { show and NoOverlappingScheduledAppointment }  for 3 but 40 DateTime, 6 int, exactly 3 ScheduledAppointment 


