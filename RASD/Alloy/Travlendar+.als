module Travlendar

open util/integer as Integer
open util/boolean as boolean
open util/ordering[Date] as DO
open util/ordering[Time] as TO
open util/ordering[ScheduledAppointment] as SAO

---------------------------------
sig Date{}
---------------------------------
sig SchedulePlan{}
---------------------------------
sig Time{}

--------------------------------

// sig Gps{}
---------------------------------

sig TimeSlot{
	start: one Time,
	end: one Time
}

---------------------------------
sig OptimizingCriteria{}

-----------------------------------------------

//dire che due appointment con user diverso non possono essere nello stesso schedule
sig User{
	hasCar: one Bool,
	hasBike: one Bool,
	hasPass: one Bool
}

fact NoUserUnlinked {
	all u : User | u in Appointment.user 
}

---------------------------------
sig Schedule{
	date: one Date,
	appointments: some ScheduledAppointment,
	wakeUpTime: one Time,
	optimizingCriteria: one OptimizingCriteria,
	constraints : set ConstraintOnSchedule
}
// all appointment of a schedule must have it as schedule
fact {
	all a : ScheduledAppointment, s : Schedule | a in s.appointments <=> a.schedule = s
}

// there aren't schedules that doesn't have at least one ScheduledAppointment
fact NoScheduleUnlinked{
	all s:Schedule | s in ScheduledAppointment.schedule  
}

// we can't have two different schedule for the same day with the same optimizing criteria
fact DifferentOptimizingCriteriaForScheduleOfTheSameDay{
	all s,s1 : Schedule| s != s1 and s.date = s1.date  => s.optimizingCriteria != s1.optimizingCriteria 
}

// no schedule of scheduled appointment belonging to different user (EDO PARE FUNZIONI)
fact belongingCoeherence{
	no sa1,sa2 :ScheduledAppointment | sa1.schedule = sa2.schedule 
	and sa1.appointment.user != sa2.appointment.user
}

---------------------------------

sig Appointment{
	user: one User,
	date: one Date,
	startingTime: lone Time,
	timeSlot: lone TimeSlot,
	constraints: set ConstraintOnAppointment
}
{	
	startingTime = none => timeSlot != none
	timeSlot = none => startingTime != none
	startingTime in TO/nexts[timeSlot.start] and startingTime in TO/prevs[timeSlot.end]	// startingTime and timeSlot are exclusive
}


// all appointments must be schedule into some scheduledAppointment
fact AppointmentAssociationCoherence {
		all a : Appointment | a in ScheduledAppointment.appointment
} // controllare operatore ESISTE

----------------------------------------

sig ScheduledAppointment{
	schedule: one Schedule,
	date: one Date,
	appointment: one Appointment,
	startingTravelTime: one Time,
	ETA: one Time,
	endingTime: one Time,
	numberOfInvolvedPeople: one Int,
	weather: one Weather
}
{	startingTravelTime in TO/prevs[endingTime]
	ETA in TO/nexts[startingTravelTime] and ETA in TO/prevs[endingTime] // ETA must be between starting and ending times
	date = schedule.date																				// same date as in its schedule
	date = appointment.date																			// same date as original appointment
//	location = appointment.location
	numberOfInvolvedPeople >= 0
	startingTravelTime in TO/nexts[schedule.wakeUpTime]							// all appointments begin after the schedule wakeup time
}

fact ScheduledAppointmentsOrderingConsistence {
	all a1,a2 : ScheduledAppointment | a1.schedule = a2.schedule and a1 in SAO/prevs[a2] => a1.startingTravelTime in TO/prevs[a2.startingTravelTime]
}

// if two scheduled appointments are relative to the same appointment then they must belong to different schedules
fact AppointmentOnMultipleSchedules {
	all s1,s2 : ScheduledAppointment | s1 != s2 and s1.appointment = s2.appointment implies s1.schedule != s2.schedule
}

// Starting time of a scheduled appointment must conincide with the starting time of the non-scheduled appointment if timeSlot is not specified
fact StartingTimeCoherence{
	all s : ScheduledAppointment | s.appointment.timeSlot = none implies s.startingTravelTime = s.appointment.startingTime
}

fact numberOfPeopleInvolvedCoherentWithSeats{
	no p : Path | p.travelMean.seats < p.source.numberOfInvolvedPeople
}

/*pred NoOverlappingScheduledAppointment {
	all s1,s2 : ScheduledAppointment |  s1.schedule = s2.schedule and s1 != s2 implies s1.endingTime in TO/prevs[s2.startingTravelTime] - s2.startingTravelTime
	or s2.endingTime in TO/prevs[s1.startingTravelTime] - s1.startingTravelTime
}*/ 

pred NoOverlappingScheduledAppointmentInSchedule (s : Schedule) {
	all s1,s2 : ScheduledAppointment |  s1.schedule = s and  s2.schedule = s and s1 != s2 implies s1.endingTime in TO/prevs[s2.startingTravelTime] - s2.startingTravelTime
	or s2.endingTime in TO/prevs[s1.startingTravelTime] - s1.startingTravelTime
}

----------------------------------------------------
//OGNI TANTO LI FAVEDERE OGNI TANTO NO NON SO IL PERCHE
sig Path{
	lenght: one Int,
	source: one ScheduledAppointment,
	dest: one ScheduledAppointment,
	travelMean: one TravelMean
}
{
	lenght >= 0
	source != dest
	source.schedule = dest.schedule // 
}

//no path linking to scheduled appointment belonging to different user (EDO PENSO VADA)
fact SchedulePathBelongingConsistency{ 
	all p:Path | p.source.appointment.user = p.dest.appointment.user
}

fact {
	all s : ScheduledAppointment | s in (Path.source + Path.dest)
}

//no path belonging to a user with a travel mean that he/she doesn't have(EDO NON SO COME SCRIVERE FALSE))
fact TravelMeanConsistency{
	no p:Path| (p.travelMean = Bike and p.source.appointment.user.hasBike in False) or 
	(p.travelMean = Car and p.source.appointment.user.hasCar in False) 
}

//no path linking two scheduled appointments that are not sequential (EDO PENSO VADA)
fact PathOrderConsistency{
	all p:Path | p.source = SAO/prev[p.dest] //penso faccia valere anche il viceversa
} 

fact SchedulePathConsistency{
	//all p1,p2 : Path | p.source.schedule = p.dest.schedule
}

-----------------------------------------------------

abstract sig Constraint{
	travelMean: one TravelMean,
	maxTravelDistance: one Int // with travel mean above
}
{maxTravelDistance >= 0} //if 0 deactivates travel mean


fact NoConstraintUnlinked{
	all c : Constraint | c in Appointment.constraints or c in Schedule.constraints
}

sig ConstraintOnAppointment extends Constraint{
}
{maxTravelDistance = 0}

sig ConstraintOnSchedule extends Constraint{
	weather: set Weather,
	timeSlot: lone TimeSlot,	 
	strikeDate: lone Bool
}{
	weather != none => maxTravelDistance = 0
	weather != none => (timeSlot .start = timeSlot.end) //scrivere elemento nullo
}
// in which travel mean can't be used

//PARTE DEL PRED CHE SERVE PER VEDERE SE ESISTE UN VALID SCHEDULE 
//CHE SODDISFA DELLE CONSTRAINTS. IL PROBLEMA E' DIVISO IN PIU' PARTI

/*fun distanceTravelledWithMeanInSchedule [s : Schedule, t: TravelMean] : Int {
	//all p:Path | doesPathBelongToSchedule[s, p]
	//let m = p:Path|doesPathBelongToSchedule[s, p] 
	x: some e | 
	sum p:(| doesPathBelongToSchedule[s, p]) | p.length 
}*/

//da aggiungere la distanza
pred doesConstraintSatisfySchedule (c : ConstraintOnSchedule){
	some s : Schedule |  all p:Path |
	doesPathBelongToSchedule[s, p] and
	p.travelMean  != c.travelMean or (p.travelMean = c.travelMean 
						   and s.appointments.weather not in c.weather 
						   and ((c.timeSlot.start = c.timeSlot.end) 
							or (s.appointments.ETA in TO/prevs[c.timeSlot.start]
							or s.appointments.startingTravelTime in TO/nexts[c.timeSlot.end] ))
						   and (c.strikeDate in True implies p.travelMean not in PublicTravelMean)
						   )
}

pred doesConstraintSatisfyAppointment(c : ConstraintOnAppointment){
	some s: Schedule | all sa:ScheduledAppointment, a: Appointment, p: Path | sa in s.appointments and sa.appointment = a and p.dest=sa => 
	p.travelMean != c.travelMean
}

pred validSchedule {
	some s: Schedule | all cs:ConstraintOnSchedule, ca:ConstraintOnAppointment  | 
	cs in s.constraints and ca in s.appointments.appointment.constraints and 
	doesConstraintSatisfySchedule[cs] and doesConstraintSatisfyAppointment[ca] and NoOverlappingScheduledAppointmentInSchedule[s]
}

pred doesPathBelongToSchedule [s:Schedule, p:Path] {
	some sa:ScheduledAppointment | sa in (Path.source + Path.dest) and sa.schedule=s
}

/*
//retrieves all the paths of each schedule
fun pathOfSchedule [s : Schedule] : set Path {
	set Path | (Path.dest + Path.source).appointments  in s
}*/

//used to find all the paths of a schedule

----------------------------------------------------

//sig Coordinates{
	//longitude: one Int,
	//latitude: one Int
//}
//{longitude >= 0 latitude >= 0}

-------------------------------------------------
/*
*********************************
METTERE SU RELAZIONE 
********************************
*/


abstract sig Weather{} // METTERE UNIQUE

sig Sunny extends Weather{}
sig Snowy extends Weather{}
sig Rainy extends Weather{}
sig Foggy extends Weather{}
sig Cloudy extends Weather{}

fact NoWeatherUnlinked {
	all w : Weather | w in ConstraintOnSchedule.weather 
}

//sig OptimizingCriteria{}
------------------------------------------------
abstract sig TravelMean{ 
	seats: one Int
}
//100 seats encode an unbounded number of seats

abstract sig PublicTravelMean extends TravelMean{}
abstract sig SharedTravelMean extends TravelMean{}
abstract sig PrivateTravelMean extends TravelMean{}

sig Train extends PublicTravelMean{}{
	seats=100
}
sig Bus extends PublicTravelMean{}{
	seats=100
}
sig Tram extends PublicTravelMean{}{
	seats=100
}
sig Taxi extends PublicTravelMean{}{
	seats=3
}
sig Underground extends PublicTravelMean{}{
	seats=100
}
sig CarSharing extends SharedTravelMean{}{
	seats=5
}
sig BikeSharing extends SharedTravelMean{}{
	seats=1
}
sig Bike extends PrivateTravelMean{}{
	seats=1
}
sig Car extends PrivateTravelMean{}{
	seats=5
}
sig Walking extends PrivateTravelMean{}{
	seats=100
}
-------------------------------------------------

assert ScheduledAppointmentAndScheduleBiunivocity {
	all a : ScheduledAppointment | all s : Schedule | a.schedule = s => a in s.appointments
}
check ScheduledAppointmentAndScheduleBiunivocity for 8
// funziona perchè fact con doppia implicazione

assert EveryAppoinmentScheduleInItsDay {
	all s : Schedule, a : ScheduledAppointment | s.date = a.date => a.schedule = s
}
//check EveryAppoinmentScheduleInItsDay for 6
-----------------------------------------------------
assert ScheduleOwnedByOnlyOneUser{
	all s1,s2:ScheduledAppointment | s1.schedule = s2.schedule implies s1.appointment.user = s2.appointment.user
}
//check ScheduleOwnedByOnlyOneUser for 10 //(è stupida perche c'è un fact che dice quasi la stessa cosa)
------------------------------------------------------
assert PathCoherence{
	all p:Path | p.dest = SAO/next[p.source]
}
//check PathCoherence for 10
---------------------------------------
assert apptScheduleSameDate {
	all a : ScheduledAppointment, s : Schedule | a.schedule = s => a.date = s.date
}
//check apptScheduleSameDate for 10

assert appointmentDateNotEqualToItsScheduledAppt {
	all a : ScheduledAppointment | a.date = a.appointment.date
}
check appointmentDateNotEqualToItsScheduledAppt for 5

//source and dest of each path must refer to the same schedule
assert sourceDestOfPathCoherence{
	all p:Path | p.dest.schedule = p.source.schedule
}
check sourceDestOfPathCoherence for 10

pred b { some s1,s2:ScheduledAppointment | s1 != s2 and s1.schedule = s2.schedule}

pred show(){}

run { show and validSchedule} for
5
but 15 Time, 4 Int

//3 but 15 Time, 10 Constraint, 6 int, 
//exactly 3 ScheduledAppointment, 3 Appointment, 3 OptimizingCriteria, exactly 15 Path


