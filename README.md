# ux4med
A project with the goal to study the user experience of Morphing Edge Drawings (MED) of graphs.
Structure:
- Examples: Contains some examples of MEDs in GIF, MP4 and MEDml (MED markup language) format.
- MED Engine: Contains a Java package MED for creating MEDs.
- Open Problem Presentation: Contains a webpage explaining the motivation of the project, see also https://tuna-pizza.github.io/ux4med/.

Requirements for the MED package:
- Requires packages jcodec, jcodec.javase for MP4 export (available via Maven)
- Requires AnimatedGifEncoder for GIF export
- MED.IO.yFilesConverter may be used to import a yfiles IGraph, requires yfiles for java

Structure of the MED package:
- MED.Graph contains necessary data structures for storing geometric graphs associated with morphing edge animations
- MED.Data contains some utility data structures
- MED.Algorithm contains several scheduling methods for MEDs (in particular, GreedyEdgeScheduler implements the algorithm by Misue & Akasaka (GD2019))
- MED.IO contains several IO functions, most importantly MEDmlReader and MEDmlWriter which can read and write certain graphml formats, respectively
- MED.Engine contains classes that compute and export MEDs

On the MEDml Format:
- Variant of the graphml format
- nodes can have additional attributes x, y (doubles) and color (hexcode)
- edges can have animation nodes as children
- animations can have attributes:
  * startTime (starting time of the morphing animation in ms, default 0.0)
  * speed (average speed of the morphing animation in px/ms, default 0.5), 
  * fullLengthTime (time during which the animation pauses at full length in ms, default 100.0)
  * period (time after which the animation repeats in ms, default 2500.0)
  * morphType (one of "COMPLETE", "PED", "LINEAR", "SINE", "COSINE", "INVERSESINE", determines the type of the morphing animation, default "N/A") 
