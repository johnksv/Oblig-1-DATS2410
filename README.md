# Oblig-1-DATS2410
First mandatory assignment in DATS 2410 - Network and cloud computing


##Implementasjon
	Server pinger klienter for å sjekke status
	
	

##Foreslåtte features
	Klienten støtter flere samtaler enn en
	Sende meldinger til bruker som er logget av (Kan føre til Gruppechat)
	Kryptering av meldinger
	Kallenavn
	Gif-implementasjon & Emjois
	
##SMessage Protocol (SMP)
Hver kommando starter med TYPE NUM

NUM definerer om det er en melding fra klient (1) eller en kommando (0)

Eks:
	
	TYPE 1
	ID til klient jeg sender til
	"Dette er en melding."
	
	TYPE 0
	CONNECT
	ID til klient jeg skal koble til
	

Første melding til server er versjon av klient

Kommandoer server skal tolke:
- REGUSER
- LOGIN
- LOGOFF
- CONNECT
- GETUSERS

Kommandoer klient skal tolke
- CONNECT
- RESPONSE
- DISCONNECT
- USERLIST
- LOGINFAIL
- LOGINSUCCESS
- STATUSUPDATE


Status codes:
- Offline: 0
- Busy: -
- Online: +


GetUsers: Each username is followed with a status code, like in the following syntax example.

Syntax for commando STATUSUPDATE:

	TYPE 0
	STATUSUPDATE
	UNAME
	0 or + or -

#Plan
Uke 6
- GUI
	
uke 7
- Dokumentasjon
- Ekstra
	
uke 8
- Se over program og dokumentasjon
- Ekstra

Intern deadline: 22. februar
Innlevering: Mandag uke 9
