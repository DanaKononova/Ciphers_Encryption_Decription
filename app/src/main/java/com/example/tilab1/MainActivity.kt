package com.example.tilab1

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    private val russianAlphabet = arrayOf(
        'а',
        'б',
        'в',
        'г',
        'д',
        'е',
        'ё',
        'ж',
        'з',
        'и',
        'й',
        'к',
        'л',
        'м',
        'н',
        'о',
        'п',
        'р',
        'с',
        'т',
        'у',
        'ф',
        'х',
        'ц',
        'ч',
        'ш',
        'щ',
        'ъ',
        'ы',
        'ь',
        'э',
        'ю',
        'я'
    )
    private val englishAlphabet = arrayOf(
        'a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j',
        'k',
        'l',
        'm',
        'n',
        'o',
        'p',
        'q',
        'r',
        's',
        't',
        'u',
        'v',
        'w',
        'x',
        'y',
        'z'
    )
    private val n_Russian = russianAlphabet.size
    private val n_English = englishAlphabet.size

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sourceTextView = findViewById<EditText>(R.id.sourceText)
        val encryptedTextView = findViewById<EditText>(R.id.encryptedText)
        val keyView = findViewById<EditText>(R.id.key)
        val cipherTextView = findViewById<TextView>(R.id.cypherText)
        val decryptionTextView = findViewById<TextView>(R.id.decryptionText)

        val sourceTextViewV = findViewById<EditText>(R.id.sourceTextV)
        val encryptedTextViewV = findViewById<EditText>(R.id.encryptedTextV)
        val keyViewV = findViewById<EditText>(R.id.keyV)
        val cipherTextViewV = findViewById<TextView>(R.id.cypherTextV)
        val decryptionTextViewV = findViewById<TextView>(R.id.decryptionTextV)

        findViewById<Button>(R.id.cypherButton).setOnClickListener {
            val key = getEnglishInput(keyView.text.toString())
            var isEmpty1 = false
            var isEmpty2 = false
            if (key.isNotEmpty()) {
                val sourceText = getEnglishInput(sourceTextView.text.toString())
                val columnCipherTable = createColumnCipherTable(sourceText, key)
                cipherTextView.text = codeColumnCipher(columnCipherTable)
            } else isEmpty1 = true

            val keyV = getRussianInput(keyViewV.text.toString())
            if (keyV.isNotEmpty()) {
                val sourceTextV = getRussianInput(sourceTextViewV.text.toString())
                cipherTextViewV.text = codeCipherVigenere(sourceTextV, keyV)
            } else isEmpty2 = true
            if (isEmpty1 && isEmpty2) Toast.makeText(this, "Введите ключ", Toast.LENGTH_LONG).show()
        }

        findViewById<Button>(R.id.decryptionButton).setOnClickListener {
            val key = getEnglishInput(keyView.text.toString())
            var isEmpty1 = false
            var isEmpty2 = false
            if (key.isNotEmpty()) {
                val encryptedText = getEnglishInput(encryptedTextView.text.toString())
                decryptionTextView.text = decodeColumnCipher(encryptedText, key)
            } else isEmpty1 = true

            val keyV = getRussianInput(keyViewV.text.toString())
            if (keyV.isNotEmpty()) {
                val encryptedTextV = getRussianInput(encryptedTextViewV.text.toString())
                decryptionTextViewV.text = decodeCipherVigenere(encryptedTextV, keyV)
            } else isEmpty2 = true
            if (isEmpty1 && isEmpty2) Toast.makeText(this, "Введите ключ", Toast.LENGTH_LONG).show()
        }

        findViewById<ImageButton>(R.id.sourceDown).setOnClickListener {
            readFromFile("sourceColumn", sourceTextView)
        }

        findViewById<ImageButton>(R.id.shirfDown).setOnClickListener {
            readFromFile("cipherColumn", encryptedTextView)
        }

        findViewById<ImageButton>(R.id.sourceDownV).setOnClickListener {
            readFromFile("sourceVigenere", sourceTextViewV)
        }

        findViewById<ImageButton>(R.id.shifrDownV).setOnClickListener {
            readFromFile("cipherVigenere", encryptedTextViewV)
        }

        findViewById<ImageButton>(R.id.sourceUp).setOnClickListener {
            File(filesDir, "cipherColumn").writeText(cipherTextView.text.toString())
            File(filesDir, "cipherVigenere").writeText(cipherTextViewV.text.toString())
        }

        findViewById<ImageButton>(R.id.shifrUp).setOnClickListener {
            File(filesDir, "sourceColumn").writeText(decryptionTextView.text.toString())
            File(filesDir, "sourceVigenere").writeText(decryptionTextViewV.text.toString())
        }
    }

    private fun readFromFile(file: String, view: EditText) {
        val myInputStream: InputStream
        val output: String
        try {
            myInputStream = assets.open(file)
            val size: Int = myInputStream.available()
            val buffer = ByteArray(size)
            myInputStream.read(buffer)
            output = String(buffer)
            view.setText(output)
        } catch (e: IOException) {
            Toast.makeText(this, "Файл не найден!!!", Toast.LENGTH_LONG).show()
        }
    }

    fun getRussianInput(text: String): String {
        var input = ""
        for (i in text.indices) {
            if (russianAlphabet.indexOf(text[i]) != -1 || (text[i] in 'А'..'Я') || text[i] == 'Ё' || text[i] == 'ё') {
                input += text[i].lowercase()
            }
        }
        return input
    }

    fun getEnglishInput(text: String): String {
        var input = ""
        for (i in text.indices) {
            if (englishAlphabet.indexOf(text[i]) != -1 || (text[i] in 'A'..'Z')) {
                input += text[i].lowercase()
            }
        }
        return input
    }

    fun removeSpaces(text: String): String = text.replace("\\s".toRegex(), "")

    fun createColumnCipherTable(phrase: String, key: String): MutableList<Array<Char>> {
        val cipherTable = mutableListOf<Array<Char>>()
        cipherTable.add(0, Array(key.length) { ' ' })
        cipherTable.add(1, Array(key.length) { ' ' })
        val sortPhrase = String(key.toCharArray().apply { sort() })
        println(sortPhrase)
        val keyMap = mutableMapOf<Char, Int>()
        for (i in sortPhrase.indices) {
            keyMap[sortPhrase[i]] = sortPhrase.indexOf(sortPhrase[i]) + 1
        }
        println(keyMap.toString())
        for (i in key.indices) {
            cipherTable[0][i] = key[i]
            cipherTable[1][i] = (keyMap.getValue(key[i]) + '0'.code).toChar()
            keyMap[key[i]] = keyMap.getValue(key[i]) + 1
        }
        var row = 2
        var column = 0
        cipherTable.add(row, Array(key.length) { ' ' })
        for (i in phrase.indices) {
            cipherTable[row][column] = phrase[i]
            if (row - 1 == cipherTable[1][column].code - '0'.code || column == key.length - 1) {
                row++
                column = -1
                cipherTable.add(row, Array(key.length) { ' ' })
            }
            column++
        }
        return cipherTable
    }

    private fun codeColumnCipher(cipherTable: MutableList<Array<Char>>): String {
        var cipherText = ""
        var char = ' '
        for (i: Int in 1..cipherTable[0].size) {
            for (j: Int in 2..cipherTable.size - 1) {
                char = cipherTable[j][cipherTable[1].indexOf((i + '0'.code).toChar())]
                if (char != ' ') {
                    cipherText += char
                }
            }
        }
        return cipherText
    }

    fun decodeColumnCipher(cipherText: String, key: String): String {
        var phrase = ""
        val reverseTable = mutableListOf<Array<Char>>()
        reverseTable.add(0, Array(key.length) { ' ' })
        reverseTable.add(1, Array(key.length) { ' ' })
        val sortPhrase = String(key.toCharArray().apply { sort() })
        println(sortPhrase)
        val keyMap = mutableMapOf<Char, Int>()
        for (i in sortPhrase.indices) {
            keyMap[sortPhrase[i]] = sortPhrase.indexOf(sortPhrase[i]) + 1
        }
        println(keyMap.toString())
        for (i in key.indices) {
            reverseTable[0][i] = key[i]
            reverseTable[1][i] = (keyMap.getValue(key[i]) + '0'.code).toChar()
            keyMap[key[i]] = keyMap.getValue(key[i]) + 1
        }
        var row = 2
        var column = 0
        reverseTable.add(row, Array(key.length) { ' ' })
        for (i in cipherText.indices) {
            reverseTable[row][column] = '*'
            if ((row - 1 == reverseTable[1][column].code - '0'.code || column == key.length - 1) && i != cipherText.length - 1) {
                row++
                column = -1
                reverseTable.add(row, Array(key.length) { ' ' })
            }
            column++
        }
        row = 2
        val endRow = reverseTable.size
        var index = 0
        for (i in key.indices) {
            for (j: Int in row until endRow) {
                column = reverseTable[1].indexOf(i.toChar() + 1 + '0'.code)
                if (reverseTable[j][column] == '*') {
                    reverseTable[j][column] = cipherText[index++]
                }
            }
        }
        for (i in reverseTable.indices) {
            println(reverseTable[i].contentDeepToString())
        }
        for (j: Int in row until endRow) {
            for (i: Int in 0 until reverseTable[j].size) {
                if (reverseTable[j][i] != ' ') {
                    phrase += reverseTable[j][i]
                }
            }
        }
        return phrase
    }

    fun codeCipherVigenere(phrase: String, key: String): String {
        var autoKey = key
        val keyLength = key.length
        for (j in 0 until phrase.length - keyLength) {
            autoKey += phrase.get(j)
        }

        var cipherText = ""
        for (i in phrase.indices) {
            cipherText += russianAlphabet[(n_Russian + russianAlphabet.indexOf(phrase[i]) + russianAlphabet.indexOf(
                autoKey[i]
            )) % n_Russian]
        }
        return cipherText
    }

    fun decodeCipherVigenere(cipherText: String, key: String): String {
        var phrase = ""
        var autoKey = key
        for (i: Int in cipherText.indices) {
            phrase += russianAlphabet[(n_Russian + russianAlphabet.indexOf(cipherText[i]) - russianAlphabet.indexOf(
                autoKey[i]
            )) % n_Russian]
            autoKey += phrase[i]
        }
        return phrase
    }
}