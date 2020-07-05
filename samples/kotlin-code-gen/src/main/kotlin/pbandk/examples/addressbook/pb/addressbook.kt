@file:UseSerializers(pbandk.ser.TimestampSerializer::class)

package pbandk.examples.addressbook.pb

import kotlinx.serialization.*
import kotlinx.serialization.json.*

data class Person(
    val name: String = "",
    val id: Int = 0,
    val email: String = "",
    val phones: List<pbandk.examples.addressbook.pb.Person.PhoneNumber> = emptyList(),
    val lastUpdated: pbandk.wkt.Timestamp? = null,
    val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message<Person> {
    override operator fun plus(other: Person?) = protoMergeImpl(other)
    override val protoSize by lazy { protoSizeImpl() }
    override fun protoMarshal(m: pbandk.Marshaller) = protoMarshalImpl(m)
    override fun jsonMarshal(json: Json) = jsonMarshalImpl(json)
    fun toJsonMapper() = toJsonMapperImpl()
    companion object : pbandk.Message.Companion<Person> {
        val defaultInstance by lazy { Person() }
        override fun protoUnmarshal(u: pbandk.Unmarshaller) = Person.protoUnmarshalImpl(u)
        override fun jsonUnmarshal(json: Json, data: String) = Person.jsonUnmarshalImpl(json, data)
    }

    @Serializable
    data class JsonMapper(
        @SerialName("name")
        val name: String? = null,
        @SerialName("id")
        val id: Int? = null,
        @SerialName("email")
        val email: String? = null,
        @SerialName("phones")
        val phones: List<pbandk.examples.addressbook.pb.Person.PhoneNumber.JsonMapper> = emptyList(),
        @SerialName("lastUpdated")
        val lastUpdated: pbandk.wkt.Timestamp.JsonMapper? = null
    ) {
        fun toMessage() = toMessageImpl()
    }

    sealed class PhoneType(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
        override fun equals(other: kotlin.Any?) = other is Person.PhoneType && other.value == value
        override fun hashCode() = value.hashCode()
        override fun toString() = "Person.PhoneType.${name ?: "UNRECOGNIZED"}(value=$value)"

        object MOBILE : PhoneType(0, "MOBILE")
        object HOME : PhoneType(1, "HOME")
        object WORK : PhoneType(2, "WORK")
        class UNRECOGNIZED(value: Int) : Person.PhoneType(value)

        companion object : pbandk.Message.Enum.Companion<Person.PhoneType> {
            val values: List<Person.PhoneType> by lazy { listOf(MOBILE, HOME, WORK) }
            override fun fromValue(value: Int) = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
            override fun fromName(name: String) = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No PhoneType with name: $name")
        }
    }

    data class PhoneNumber(
        val number: String = "",
        val type: pbandk.examples.addressbook.pb.Person.PhoneType = pbandk.examples.addressbook.pb.Person.PhoneType.fromValue(0),
        val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
    ) : pbandk.Message<Person.PhoneNumber> {
        override operator fun plus(other: Person.PhoneNumber?) = protoMergeImpl(other)
        override val protoSize by lazy { protoSizeImpl() }
        override fun protoMarshal(m: pbandk.Marshaller) = protoMarshalImpl(m)
        override fun jsonMarshal(json: Json) = jsonMarshalImpl(json)
        fun toJsonMapper() = toJsonMapperImpl()
        companion object : pbandk.Message.Companion<Person.PhoneNumber> {
            val defaultInstance by lazy { Person.PhoneNumber() }
            override fun protoUnmarshal(u: pbandk.Unmarshaller) = Person.PhoneNumber.protoUnmarshalImpl(u)
            override fun jsonUnmarshal(json: Json, data: String) = Person.PhoneNumber.jsonUnmarshalImpl(json, data)
        }

        @Serializable
        data class JsonMapper(
            @SerialName("number")
            val number: String? = null,
            @SerialName("type")
            val type: String? = null
        ) {
            fun toMessage() = toMessageImpl()
        }
    }
}

data class AddressBook(
    val people: List<pbandk.examples.addressbook.pb.Person> = emptyList(),
    val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message<AddressBook> {
    override operator fun plus(other: AddressBook?) = protoMergeImpl(other)
    override val protoSize by lazy { protoSizeImpl() }
    override fun protoMarshal(m: pbandk.Marshaller) = protoMarshalImpl(m)
    override fun jsonMarshal(json: Json) = jsonMarshalImpl(json)
    fun toJsonMapper() = toJsonMapperImpl()
    companion object : pbandk.Message.Companion<AddressBook> {
        val defaultInstance by lazy { AddressBook() }
        override fun protoUnmarshal(u: pbandk.Unmarshaller) = AddressBook.protoUnmarshalImpl(u)
        override fun jsonUnmarshal(json: Json, data: String) = AddressBook.jsonUnmarshalImpl(json, data)
    }

    @Serializable
    data class JsonMapper(
        @SerialName("people")
        val people: List<pbandk.examples.addressbook.pb.Person.JsonMapper> = emptyList()
    ) {
        fun toMessage() = toMessageImpl()
    }
}

fun Person?.orDefault() = this ?: Person.defaultInstance

private fun Person.protoMergeImpl(plus: Person?): Person = plus?.copy(
    phones = phones + plus.phones,
    lastUpdated = lastUpdated?.plus(plus.lastUpdated) ?: plus.lastUpdated,
    unknownFields = unknownFields + plus.unknownFields
) ?: this

private fun Person.protoSizeImpl(): Int {
    var protoSize = 0
    if (name.isNotEmpty()) protoSize += pbandk.Sizer.tagSize(1) + pbandk.Sizer.stringSize(name)
    if (id != 0) protoSize += pbandk.Sizer.tagSize(2) + pbandk.Sizer.int32Size(id)
    if (email.isNotEmpty()) protoSize += pbandk.Sizer.tagSize(3) + pbandk.Sizer.stringSize(email)
    if (phones.isNotEmpty()) protoSize += (pbandk.Sizer.tagSize(4) * phones.size) + phones.sumBy(pbandk.Sizer::messageSize)
    if (lastUpdated != null) protoSize += pbandk.Sizer.tagSize(5) + pbandk.Sizer.messageSize(lastUpdated)
    protoSize += unknownFields.entries.sumBy { it.value.size() }
    return protoSize
}

private fun Person.protoMarshalImpl(protoMarshal: pbandk.Marshaller) {
    if (name.isNotEmpty()) protoMarshal.writeTag(10).writeString(name)
    if (id != 0) protoMarshal.writeTag(16).writeInt32(id)
    if (email.isNotEmpty()) protoMarshal.writeTag(26).writeString(email)
    if (phones.isNotEmpty()) phones.forEach { protoMarshal.writeTag(34).writeMessage(it) }
    if (lastUpdated != null) protoMarshal.writeTag(42).writeMessage(lastUpdated)
    if (unknownFields.isNotEmpty()) protoMarshal.writeUnknownFields(unknownFields)
}

private fun Person.Companion.protoUnmarshalImpl(protoUnmarshal: pbandk.Unmarshaller): Person {
    var name = ""
    var id = 0
    var email = ""
    var phones: pbandk.ListWithSize.Builder<pbandk.examples.addressbook.pb.Person.PhoneNumber>? = null
    var lastUpdated: pbandk.wkt.Timestamp? = null
    while (true) when (protoUnmarshal.readTag()) {
        0 ->
            return Person(
                name, id, email, pbandk.ListWithSize.Builder.fixed(phones),
                lastUpdated, protoUnmarshal.unknownFields()
            )
        10 -> name = protoUnmarshal.readString()
        16 -> id = protoUnmarshal.readInt32()
        26 -> email = protoUnmarshal.readString()
        34 -> phones = protoUnmarshal.readRepeatedMessage(phones, pbandk.examples.addressbook.pb.Person.PhoneNumber.Companion, true)
        42 -> lastUpdated = protoUnmarshal.readMessage(pbandk.wkt.Timestamp.Companion)
        else -> protoUnmarshal.unknownField()
    }
}

private fun Person.toJsonMapperImpl(): Person.JsonMapper =
    Person.JsonMapper(
        name.takeIf { it != "" },
        id,
        email.takeIf { it != "" },
        phones.map { it.toJsonMapper() },
        lastUpdated?.toJsonMapper()
    )

private fun Person.JsonMapper.toMessageImpl(): Person {
    return Person(
        name = name ?: "",
        id = id ?: 0,
        email = email ?: "",
        phones = phones.map { it.toMessage() },
        lastUpdated = lastUpdated?.toMessage()
    )
}

private fun Person.jsonMarshalImpl(json: Json): String =
    json.stringify(Person.JsonMapper.serializer(), toJsonMapper())

private fun Person.Companion.jsonUnmarshalImpl(json: Json, data: String): Person {
    val mapper = json.parse(Person.JsonMapper.serializer(), data)
    return mapper.toMessage()
}

fun Person.PhoneNumber?.orDefault() = this ?: Person.PhoneNumber.defaultInstance

private fun Person.PhoneNumber.protoMergeImpl(plus: Person.PhoneNumber?): Person.PhoneNumber = plus?.copy(
    unknownFields = unknownFields + plus.unknownFields
) ?: this

private fun Person.PhoneNumber.protoSizeImpl(): Int {
    var protoSize = 0
    if (number.isNotEmpty()) protoSize += pbandk.Sizer.tagSize(1) + pbandk.Sizer.stringSize(number)
    if (type.value != 0) protoSize += pbandk.Sizer.tagSize(2) + pbandk.Sizer.enumSize(type)
    protoSize += unknownFields.entries.sumBy { it.value.size() }
    return protoSize
}

private fun Person.PhoneNumber.protoMarshalImpl(protoMarshal: pbandk.Marshaller) {
    if (number.isNotEmpty()) protoMarshal.writeTag(10).writeString(number)
    if (type.value != 0) protoMarshal.writeTag(16).writeEnum(type)
    if (unknownFields.isNotEmpty()) protoMarshal.writeUnknownFields(unknownFields)
}

private fun Person.PhoneNumber.Companion.protoUnmarshalImpl(protoUnmarshal: pbandk.Unmarshaller): Person.PhoneNumber {
    var number = ""
    var type: pbandk.examples.addressbook.pb.Person.PhoneType = pbandk.examples.addressbook.pb.Person.PhoneType.fromValue(0)
    while (true) when (protoUnmarshal.readTag()) {
        0 -> return Person.PhoneNumber(number, type, protoUnmarshal.unknownFields())
        10 -> number = protoUnmarshal.readString()
        16 -> type = protoUnmarshal.readEnum(pbandk.examples.addressbook.pb.Person.PhoneType.Companion)
        else -> protoUnmarshal.unknownField()
    }
}

private fun Person.PhoneNumber.toJsonMapperImpl(): Person.PhoneNumber.JsonMapper =
    Person.PhoneNumber.JsonMapper(
        number.takeIf { it != "" },
        type?.name
    )

private fun Person.PhoneNumber.JsonMapper.toMessageImpl(): Person.PhoneNumber {
    return Person.PhoneNumber(
        number = number ?: "",
        type = type?.let { pbandk.examples.addressbook.pb.Person.PhoneType.fromName(it) } ?: pbandk.examples.addressbook.pb.Person.PhoneType.fromValue(0)
    )
}

private fun Person.PhoneNumber.jsonMarshalImpl(json: Json): String =
    json.stringify(Person.PhoneNumber.JsonMapper.serializer(), toJsonMapper())

private fun Person.PhoneNumber.Companion.jsonUnmarshalImpl(json: Json, data: String): Person.PhoneNumber {
    val mapper = json.parse(Person.PhoneNumber.JsonMapper.serializer(), data)
    return mapper.toMessage()
}

fun AddressBook?.orDefault() = this ?: AddressBook.defaultInstance

private fun AddressBook.protoMergeImpl(plus: AddressBook?): AddressBook = plus?.copy(
    people = people + plus.people,
    unknownFields = unknownFields + plus.unknownFields
) ?: this

private fun AddressBook.protoSizeImpl(): Int {
    var protoSize = 0
    if (people.isNotEmpty()) protoSize += (pbandk.Sizer.tagSize(1) * people.size) + people.sumBy(pbandk.Sizer::messageSize)
    protoSize += unknownFields.entries.sumBy { it.value.size() }
    return protoSize
}

private fun AddressBook.protoMarshalImpl(protoMarshal: pbandk.Marshaller) {
    if (people.isNotEmpty()) people.forEach { protoMarshal.writeTag(10).writeMessage(it) }
    if (unknownFields.isNotEmpty()) protoMarshal.writeUnknownFields(unknownFields)
}

private fun AddressBook.Companion.protoUnmarshalImpl(protoUnmarshal: pbandk.Unmarshaller): AddressBook {
    var people: pbandk.ListWithSize.Builder<pbandk.examples.addressbook.pb.Person>? = null
    while (true) when (protoUnmarshal.readTag()) {
        0 -> return AddressBook(pbandk.ListWithSize.Builder.fixed(people), protoUnmarshal.unknownFields())
        10 -> people = protoUnmarshal.readRepeatedMessage(people, pbandk.examples.addressbook.pb.Person.Companion, true)
        else -> protoUnmarshal.unknownField()
    }
}

private fun AddressBook.toJsonMapperImpl(): AddressBook.JsonMapper =
    AddressBook.JsonMapper(
        people.map { it.toJsonMapper() }
    )

private fun AddressBook.JsonMapper.toMessageImpl(): AddressBook {
    return AddressBook(
        people = people.map { it.toMessage() }
    )
}

private fun AddressBook.jsonMarshalImpl(json: Json): String =
    json.stringify(AddressBook.JsonMapper.serializer(), toJsonMapper())

private fun AddressBook.Companion.jsonUnmarshalImpl(json: Json, data: String): AddressBook {
    val mapper = json.parse(AddressBook.JsonMapper.serializer(), data)
    return mapper.toMessage()
}
