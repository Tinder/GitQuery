## Gradle Proto to Kotlin Code Gen 

Taken from the the [streem/pbandk](https://github.com/streem/pbandk), this example uses git query to sync the address 
book example in the [protobuf repository](https://github.com/google/protobuf) and use [streem/pbandk](https://github.com/streem/pbandk)
 to generate Kotlin. 
 
For this example the synced proto and generated code has been committed to show what it looks like.

### Building

To build the code simply run:

    path/to/gradle installDist

### Running

Once built, the start script will be at `build/install/addressbook/bin/addressbook` (and `.bat` version on Windows).
There are two commands that both require filenames, `add-person` and `list-people`. To add a person to a file called
`sample-book`, run:

    build/install/addressbook/bin/addressbook add-person sample-book

Run multiple times to add multiple people. To see the list of people saved in `sample-book`, run:

    build/install/addressbook/bin/addressbook list-people sample-book