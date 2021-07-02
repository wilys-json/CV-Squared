## CV-Squared: Core Vocabulary with Computer Vision

![CV-Squared Example - Clothing Store](sample.gif)

### Introduction

The current project serves as a prototype of a work-in-progress of what can
ultimately become an Augmentative and Alternative Communication system/application
that is context-sensitive and individualized, making use of multiple cutting-edge
algorithms in the field of artificial intelligence and machine learning, one of
which being Computer Vision.

### Concepts

**Core Vocabulary**

Core Vocabulary is an evidence-based intervention approach in the field of
Speech Language Therapy (or Speech Language Pathology), particulary in
Augmentative and Alternative Communication (AAC), which enhances overall
intelligibility (augmentative) or compensate limited or absent speech ability
(alternative) of an individual with speech and language disorder resulting
from acquired neurological conditions such as a stroke (e.g. aphasia and severe
apraxia or dysarthria) or neuro-developmental disorder such as Autism Spectrum
Disorder (ASD), Cerebral Palsy (CP). It simply means the common words or
activities that people use to communicate or do in a certain place, context,
or an occasion, e.g. in a fastfood restaurant we often talk about `hamburgers`,
`coke` or other drinks, or in a cinema we `buy movie tickets` or a `movie`.


To use core vocabulary for such purpose, we need an AAC device or system. An AAC
device or system that draws on (advanced) technological resources is known
as high-tech AAC. These systems are run on standalone devices or an application
on edge devices such as a smartphone or tablet (e.g. [Proloque2Go](https://apps.apple.com/us/app/proloquo2go/id308368164)).
One limitation in these systems however are operations such as flipping for
categories and clicking item and speech generation buttons could be obstacles
for individuals with a comorbid (fine-)motor impairment or developmental delay.
Another problem with existing AAC system is that they are insensitive to the
physical context and too generic (or takes too many steps to customize). These
make the implementation of Core Vocabulary on high-tech AAC system way less
"user-friendly".


**Computer Vision & Indoor Scene Recognition**

Recent advancement in machine learning, particularly in the field of Computer
Vision, would best come to rescue. Machine Learning algorithms such as a
Convolutional Neural Network (CNN) allow computers to "see" and "understand"
visual objects just as human do. Specifically, deploying an indoor scene
Recognition or classification model on edge devices would allow a computer
system to look for context relevant content for an AAC user.


### Project development

The project could thought as a three-phase process:


*Model Training*

This part originates from [the Capstone project of Udacity's Machine Learning
Engineering Nanodegree](https://github.com/wlamcuhk/indoor-scene-recognition), where I trained EfficientNet-b3 on MIT Indoor 67.
Instead of quantizing the model, I trained another EfficientNet-lite2 model (with
a 80:20 train-test split) in the PyTorch framework on AWS SageMaker.
The model achieved a Top-1 test accuracy of 80.4%


*Deployment & Prototyping (Current phase)*

The model is then deployed on an Android application called "SceneRecogCam"
(short for Scene Recognition Camera). As of the current progress, being part of
Harvard CS50 final project, the application could be seen a prototype.


*Testing*

The prototype will hopefully be tested with target end-users, i.e. individuals
with limited or no speech, and then fine-tuned or optimized.


### How the application works

*Image Capture*

User can first capture the current scene using the back facing camera. The image
will then be converted to a Bitmap.


*Image Processing*

The Bitmap is rotated w.r.t. orientation and resized to a 3 x 260 x 260
Tensor array.


*Scene Recognition*

The processed image (Tensor) is then fed into the deployed CNN model
(EfficientNet-lite2). The application will then classify (recognize) the most
probable scene among the 67 scenes in the MIT indoor 67 dataset and return the
label on together with the rotated Bitmap image.


*Knowledge Graph Query*

The application then sends an API request to [ConceptNet5](https://conceptnet.io/),
an open-source knowledge graph, with the resulting scene label as the body and
query the most common `AtLocation` object. This will result in a list of common
objects found in the recognized scene and in turn become the AAC item content.


*Word Stemming*

Since there are duplicated entries or similar concepts, the application uses
a simple word stemmer to remove any duplicated word items, e.g. `a computer` &
`computers` will be collapsed into one entry. This will be set the `Text` of
the AAC item.


*Graphic Bank Query*

Upon word stemming, the application sends another API query to [OpenSymbol](https://www.opensymbols.org/)
with list of object names and get a corresponding graphic image. This will be
set as the `Image` of the AAC item.


### Future Developments

The current application serves as only a prototype of an ongoing project. The
planned developments include:

*(1) Expanding Scenes Categories*: This will be done by training another model
using a massive dataset, e.g. Place365

*(2) Improving Model Accuracy & ML Pipeline*: Building a pipeline to continuously
train and test the model, and continuously optimize model training

*(3) Knolwedge Graph Building*: Building or refining a knowledge graph that is
able to reason activities, objects or common concepts associated with scenes

*(4) NLP techniques*: Word stemming is relatively a naive implementation of
semantic relations of word; more sophisticated NLP techniques such as POS tagging
and Word Vector learning shall be incorporated

*(5) Speech Generation*: Resulting texts can be read aloud using TTS service
such as AWS Polly or a personalized / customized synthesized speech

*(6) Recommendation system*: Train a model or an agent that learns the in-the-scene
preference and/or daily routine of the individual to formulate a personalized
knowledge graph / network

*(7) Image segmentation or object detection*: incorporate a YOLO model or image
segmentation model so that objects in the scene could be detected and recognized
