package uk.co.ben_gibson.repositorymapper.Host.Url.Factory;

import org.jetbrains.annotations.NotNull;
import uk.co.ben_gibson.repositorymapper.Context.Context;
import uk.co.ben_gibson.repositorymapper.Host.Url.Exception.ProjectNotFoundException;
import uk.co.ben_gibson.repositorymapper.Repository.Exception.RemoteNotFoundException;
import java.net.*;

/**
 * Creates a URL in a format expected by Stash.
 */
public class Stash implements Factory
{

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public URL createUrl(@NotNull Context context, boolean forceSSL) throws MalformedURLException, URISyntaxException, RemoteNotFoundException, ProjectNotFoundException {
        URL remoteUrl = context.getRepository().getOriginUrl(forceSSL);

        String[] parts = remoteUrl.getPath().split("/", 3);

        if (parts.length < 3) {
            throw new ProjectNotFoundException(remoteUrl);
        }

        String projectName    = parts[1];
        String repositoryName = parts[2];

        String path = String.format(
            "/projects/%s/repos/%s/browse%s",
            projectName,
            repositoryName,
            context.getFilePathRelativeToRepository()
        );

        String query = "at=refs/heads/" + context.getBranch();

        String fragment = null;

        if (context.getCaretLinePosition() != null) {
            fragment = context.getCaretLinePosition().toString();
        }

        return new URI(remoteUrl.getProtocol(), remoteUrl.getHost(), path, query, fragment).toURL();
    }
}