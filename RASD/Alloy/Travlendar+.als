module Travlendar

open util/integer as Intege
open util/boolean as boolean

// impose ordering among relations
open util/ordering[Date] as DO 
open util/ordering[Time] as TO
open util/ordering[ScheduledAppointment] as SAO

-----------------------------------------------
sig Date{}

-----------------------------------------------
sig Time{}

-----------------------------------------------
sig TimeSlot{
	start: one Time,
	end: one Time
}

-----------------------------------------------
sig OptimizingCriteria{}

-----------------------------------------------
sig User{
	hasCar: one Bool,
	hasBike: one Bool,
	hasPass: one Bool
}

fact NoUserUnlinked {
	all u : User | u in Appointment.user 
}

-----------------------------------------------
sig Schedule{
	date: one Date,
	appointments: some ScheduledAppointment,
	wakeUpTime: one Time,
	optimizingCriteria: one OptimizingCriteria,
	constraints : set ConstraintOnSchedule,
	initialNumberOfPeopleInvolved : one Int
}

//if a Schedule has a reference to a ScheduledAppointment then trasposed relation should hold
fact ScheduleCoherenceWithScheduledAppointment{
	appointments = ~(schedule)
}

// there aren't schedules that doesn't have at least one ScheduledAppointment
fact NoScheduleUnlinked{
	all s:Schedule | s in ScheduledAppointment.schedule  
}

// we can't have two different schedule for the same day with the same optimizing criteria
fact DifferentOptimizingCriteriaForScheduleOfTheSameDay{
	all s,s1 : Schedule| s != s1 and s.date = s1.date  => s.optimizingCriteria != s1.optimizingCriteria 
}

// no schedule of scheduled appointment belonging to different users
fact belongingCoeherence{
	no sa1,sa2 :ScheduledAppointment | sa1.schedule = sa2.schedule 
	and sa1.appointment.user != sa2.appointment.user
}

-----------------------------------------------
sig Appointment{
	user: one User,
	date: one Date,
	startingTime: lone Time,
	timeSlot: lone TimeSlot,
	constraints: set ConstraintOnAppointment,
	variationNumberInvolvedPeople: one Int
}
{	
	// startingTime and timeSlot are mutually exclusive
	startingTime = none => timeSlot != none
	timeSlot = none => startingTime != none
	startingTime in TO/nexts[timeSlot.start] and startingTime in TO/prevs[timeSlot.end]
}

// all appointments must be scheduled into some ScheduledAppointment
fact AppointmentAssociationCoherence {
	all a : Appointment | a in ScheduledAppointment.appointment
}

-----------------------------------------------
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
	// ETA must be between starting and ending times
	ETA in TO/nexts[startingTravelTime] and ETA in TO/prevs[endingTime] 
	date = schedule.date																				
	date = appointment.date																			
	numberOfInvolvedPeople >= 0
	startingTravelTime in TO/nexts[schedule.wakeUpTime]
}

// appointments starting time ordering coherence
fact ScheduledAppointmentsOrderingConsistence {
	all a1,a2 : ScheduledAppointment | a1.schedule = a2.schedule and a1 in SAO/prevs[a2] 
	=> a1.startingTravelTime in TO/prevs[a2.startingTravelTime]
}

// if two scheduled appointments are relative to the same appointment then they must 
//belong to different schedules
fact AppointmentOnMultipleSchedules {
	all s1,s2 : ScheduledAppointment | s1 != s2 and s1.appointment = s2.appointment 
	=> s1.schedule != s2.schedule
}

// Starting time of a scheduled appointment must coincide with the starting time of the 
//non-scheduled appointment if timeSlot is not specified
fact StartingTimeCoherence{
	all s : ScheduledAppointment | s.appointment.timeSlot = none =>
	s.startingTravelTime = s.appointment.startingTime
}

// The number of involved people in each travel is coherent with the number of seats of each 
//travel mean used for it
fact numberOfPeopleInvolvedCoherentWithSeats{
	no p : Path | p.travelMean.seats < p.source.numberOfInvolvedPeople
}

// The selected travel mean for a path can provide enough seats for the people involved in the appointment
fact coherenceOnNumberOfInvolvedPeople{
	all sa:ScheduledAppointment, a:Appointment, s:Schedule | 
	sa in s.appointments and a in sa.appointment =>
	sa.numberOfInvolvedPeople = add[s.initialNumberOfPeopleInvolved, sum e : SAO/prevs[sa] | e.appointment.variationNumberInvolvedPeople]
}

----------------------------------------------------
sig Path{
	lenght: one Int,
	source: one ScheduledAppointment,
	dest: one ScheduledAppointment,
	travelMean: one TravelMean
}
{
	lenght >= 0
	source != dest
	source.schedule = dest.schedule
}

// No path linking to scheduled appointment belonging to different user
fact SchedulePathBelongingConsistency{ 
	all p:Path | p.source.appointment.user = p.dest.appointment.user
}

// No scheduled appointment unreachable
fact AllScheduledAppointmentCanBeReachedByPaths{
	all s : ScheduledAppointment | s in (Path.source + Path.dest)
}

// No path belonging to a user with a travel mean that he/she doesn't own
fact TravelMeanConsistency{
	no p:Path| (p.travelMean = Bike and p.source.appointment.user.hasBike in False) or 
	(p.travelMean = Car and p.source.appointment.user.hasCar in False) 
}

// No path linking two scheduled appointments that are not sequential
fact PathOrderConsistency{
	all p:Path | p.source = SAO/prev[p.dest] 
} 

-----------------------------------------------------
abstract sig Constraint{
	travelMean: one TravelMean,
	maxTravelDistance: one Int // with travel mean above
}
{maxTravelDistance >= 0} //if 0 implies that the travel mean is deactivated

sig ConstraintOnAppointment extends Constraint{
}
{maxTravelDistance = 0}

sig ConstraintOnSchedule extends Constraint{
	weather: set Weather, 
	timeSlot: lone TimeSlot,	 
	strikeDate: lone Bool
	// in which travel mean can't be used
}{
	weather != none => maxTravelDistance = 0
	weather != none => (timeSlot .start = timeSlot.end)
}

fact NoConstraintUnlinked{
	all c : Constraint | c in Appointment.constraints or c in Schedule.constraints
}

-----------------------------------------------
abstract sig Weather{} 

one sig  Sunny extends Weather{}
one sig Snowy extends Weather{}
one sig Rainy extends Weather{}
one sig Foggy extends Weather{}
one sig Cloudy extends Weather{}

fact NoWeatherUnlinked {
	all w : Weather | w in ConstraintOnSchedule.weather 
}

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

-----------------------------------------------
// if a schedule appointment is on a schedule, it must be in the list of scheduled appointments of that schedule
ScheduledAppointmentAndScheduleBiunivocity : check{
	all a : ScheduledAppointment | all s : Schedule | a.schedule = s => a in s.appointments
} for 8
-----------------------------------------------------
// if a scheduled appointment is in a schedule, they must have the same date
EveryAppoinmentOfScheduleIsInItsDay : check {
	all s : Schedule, a : ScheduledAppointment | a.schedule = s => s.date = a.date
}
for 8
-----------------------------------------------------
ScheduleIsOwnedByOnlyOneUser : check {
	all s1,s2:ScheduledAppointment | s1.schedule = s2.schedule implies s1.appointment.user = s2.appointment.user
} for 8
-----------------------------------------------------
// all scheduled appointment date must be equal to its original appointment date
AppointmentDateIsEqualToItsScheduledAppt : check {
	all a : ScheduledAppointment | a.date = a.appointment.date
} for 8
-----------------------------------------------------
//source and dest of each path must refer to the same schedule
sourceDestOfPathCoherence : check {
	all p:Path | p.dest.schedule = p.source.schedule
} for 8
-----------------------------------------------------
// scheduled appointments are ordered according to their path precedence
PathOrderConsistency : check {
	all p:Path | p.dest = SAO/next[p.source]
} for 8
-----------------------------------------------------
/* if two appointments belong to different user then they should belong to different 
    schedule since a schedule belongs to only one user */ 
TwoAppointmentsWithSameUserNoInSameSchedule : check {
	all a,a':Appointment | a.user != a'.user => appointments.appointment.a & appointments.appointment.a' = none
} for 8
-----------------------------------------------------

// return all the paths of the specified schedule
fun pathOfSchedule [s : Schedule] : set Path {
	(source+dest).(s.appointments)
}

// return the total distance travelled in the specified schedule with a specific travel mean
fun distanceTravelledWithMeanInSchedule [s : Schedule, t: TravelMean] : Int {
	sum e : (pathOfSchedule[s]) & (travelMean.t) | e.lenght
}

// checks if the specified path links two scheduled appointment in the given schedule
pred doesPathBelongToSchedule [s:Schedule, p:Path] {
	some sa:ScheduledAppointment | sa in (Path.source + Path.dest) and sa.schedule=s
}

// checks if a schedule satisfying the specified constraint exists
pred doesConstraintSatisfySchedule (c : ConstraintOnSchedule){
	some s : Schedule |  all p:Path |
	doesPathBelongToSchedule[s, p] and
	p.travelMean  != c.travelMean or (p.travelMean = c.travelMean 
					and s.appointments.weather not in c.weather 
					and ((c.timeSlot.start = c.timeSlot.end) 
						or (s.appointments.ETA in TO/prevs[c.timeSlot.start]
						or s.appointments.startingTravelTime in TO/nexts[c.timeSlot.end] ))
					and (c.strikeDate in True implies p.travelMean not in PublicTravelMean)
					and (distanceTravelledWithMeanInSchedule[s,c.travelMean] < c.maxTravelDistance )
				)
}

// checks if a scheduled appointment satisfying the specified constraint exists
pred doesConstraintSatisfiesAppointment(c : ConstraintOnAppointment){
	some s: Schedule | all sa:ScheduledAppointment, a: Appointment, p: Path |
		sa in s.appointments and sa.appointment = a and p.dest=sa
		=> p.travelMean != c.travelMean
}

pred NoOverlappingScheduledAppointmentInSchedule (s : Schedule) {
	all s1,s2 : ScheduledAppointment |  s1.schedule = s and  s2.schedule = s and s1 != s2 
	=> s1.endingTime in TO/prevs[s2.startingTravelTime] - s2.startingTravelTime
	or s2.endingTime in TO/prevs[s1.startingTravelTime] - s1.startingTravelTime
}

// checks if a valid schedule exists (see def. on RASD document)
pred validSchedule {
	some s: Schedule | all cs:ConstraintOnSchedule, ca:ConstraintOnAppointment  | 
	cs in s.constraints and ca in s.appointments.appointment.constraints
	and doesConstraintSatisfySchedule[cs] and doesConstraintSatisfiesAppointment[ca]
	and NoOverlappingScheduledAppointmentInSchedule[s]
}
-----------------------------------------------------

// new appointment insertion action
pred insertNewAppointment[u,u' : User, a' : Appointment]{
	//precondition
	all a : Appointment | a.user = u and a'.user != a.user
	//postcondition
	user.u' = user.u + a'
}

// checks if the insertion of a new appointment is correct
insertNewAppointmentIsCorrect : check {
	all u,u' : User, a,a' : Appointment | a.user = u and a'.user != a.user and insertNewAppointment[u,u',a'] 
	=> a' not in user.u and a' in user.u'
} for 8
-----------------------------------------------------

// editing appointment action
pred modifyAppointment[a,a' : Appointment, u : User]{
	//precondition
	a in user.u and a' not in user.u
	//postcondition
	a' in user.u and a not in user.u
}

// checks if the modification of a new appointment is correct
modifyAppointmentIsCorrect : check {
	all u : User, a,a' : Appointment | a in user.u and a' not in user.u and modifyAppointment[a,a',u]
	=> a' in user.u and a not in user.u
} for 8


pred show(){}

run { show and validSchedule} for 6 but 4 Int
