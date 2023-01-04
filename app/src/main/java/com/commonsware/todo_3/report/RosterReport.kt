package com.commonsware.todo_3.report

import android.content.Context
import android.net.Uri
import com.commonsware.todo_3.R
import com.commonsware.todo_3.repo.ToDoModel
import com.github.jknack.handlebars.Handlebars
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RosterReport(
    private val context: Context,
    engine: Handlebars,
    private val appScope: CoroutineScope
) {
    private val template =
        engine.compileInline(context.getString(R.string.report_template))

    /**
     * - Open an OutputStream on the location specified by the Uri
     * - Wrap that in an OutputStreamWriter
     * - Call use() on the writer to automatically close it when we are done
     * - Call apply() on our template to have it generate the HTML for our models
     * - Write that to the OutputStreamWriter (page 465)
     * */
    suspend fun generate(content: List<ToDoModel>, doc: Uri) {
        withContext(Dispatchers.IO + appScope.coroutineContext) {
            context.contentResolver.openOutputStream(doc, "rwt")?.writer()?.use { osw ->
                osw.write(template.apply(content))
                osw.flush()
            }
        }
    }
}
