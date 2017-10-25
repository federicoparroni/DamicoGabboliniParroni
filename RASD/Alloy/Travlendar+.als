module Travlendar

open util/integer as Integer
open util/boolean as boolean
open util/ordering[Date] as DO
open util/ordering[Time] as TO

sig Date{}

sig Time{}

sig Schedule{
	date: one Date,
	wakeUpTime: one Time,
	scheduledAppointmentList: some ScheduledAppointment,
	startingLocation: one Coordinates
}
fact AppointmentListCoherence {
	all s : Schedule | scheduledAppointmentList.schedule	// all scheduled appointments must have itself as schedule
}

sig Appointment{
	date: one Date,
	startingTime: lone Time,
	timeSlot: lone TimeSlot,
	location: one Coordinates,
	numberInvolvedPeople: one Int,
	constraints: some Constraint
}
{	numberInvolvedPeople >= 0
	startingTime = none => timeSlot != none
	timeSlot = none => startingTime != none
	startingTime in TO/nexts[timeSlot.start] and startingTime in TO/prevs[timeSlot.end]	// startingTime and timeSlot are exclusive
}

sig ScheduledAppointment{
	schedule: one Schedule,
	date: one Date,
	appointment: one Appointment,
	startingTravelTime: one Time,
	ETA: one Time,
	endingTime: one Time,
	location: one Coordinates
}
{	startingTravelTime in TO/prevs[endingTime]
	ETA in TO/nexts[startingTravelTime] and ETA in TO/prevs[endingTime] // ETA must be between starting and ending times
	date = schedule.date																				// ScheduleAppointmentsDateCoherence
	startingTravelTime in TO/prevs[schedule.wakeUpTime]							// all appointments begin after the schedule wakeup time
}


// if two scheduled appointments are relative to the same appointment then they must belong to different schedules
fact AppointmentOnMultipleSchedules {
	all s1,s2 : ScheduledAppointment | s1 != s2 and s1.appointment = s2.appointment implies s1.schedule != s2.schedule
}

pred NoOverlappingScheduledAppointment {
	all s1,s2 : ScheduledAppointment |  s1.schedule = s2.schedule and s1 != s2 implies s1.endingTime in TO/prevs[s2.startingTravelTime] - s2.startingTravelTime
	or s2.endingTime in TO/prevs[s1.startingTravelTime] - s1.startingTravelTime
} 

// Starting time of a scheduled appointment must conincide with the starting time of the non-scheduled appointment if timeSlot is not specified
fact StartingTimeCoherence{
	all s : ScheduledAppointment | s.appointment.timeSlot = none implies s.startingTravelTime = s.appointment.startingTime
}



sig Path{
	lenght: one Int,
	source: one ScheduledAppointment,
	dest: one ScheduledAppointment
}
{
	lenght >= 0
	source != dest
}

abstract sig Constraint{
	travelMean: one TravelMean,
	maxTravelDistance: one Int // with travel mean above
}
{maxTravelDistance >= 0} //if 0 deactivates travel mean

sig ConstraintOnAppointment extends Constraint{
}

sig ConstraintOnSchedule extends Constraint{
	weather: some Weather, // in which travel mean can be used
	timeSlot: one TimeSlot // in which travel mean can be used
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


run { show and NoOverlappingScheduledAppointment } for 3 but 5 Date, 15 Time, 1 Constraint, 6 int, exactly 3 ScheduledAppointment, 3 Appointment


