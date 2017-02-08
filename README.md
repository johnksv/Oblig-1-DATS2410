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
- LOGOF
- CONNECT
- GETUSERS

Kommandoer klient skal tolke
- CONNECT
- DISCONNECT
- USERLIST
- LOGINFAIL
- LOGINSUCCESS

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
